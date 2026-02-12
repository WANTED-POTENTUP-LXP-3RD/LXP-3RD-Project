from collections import Counter
from dataclasses import dataclass
from math import log
from pathlib import Path
from typing import Any
from uuid import uuid4
import os
import re
import time

import ffmpeg
import requests
from dotenv import load_dotenv
from fastapi import BackgroundTasks, FastAPI, HTTPException
from faster_whisper import WhisperModel
from pydantic import BaseModel, Field

BASE_DIR = Path(__file__).resolve().parents[1]
load_dotenv(BASE_DIR / ".env")

app = FastAPI()

HTTP_CONNECT_TIMEOUT = float(os.getenv("JAVA_CONNECT_TIMEOUT", "5"))
HTTP_READ_TIMEOUT = float(os.getenv("JAVA_READ_TIMEOUT", "30"))
ANALYSIS_CALLBACK_SECRET = os.getenv("ANALYSIS_CALLBACK_SECRET", "")
S3_OBJECT_BASE_URL = os.getenv("S3_OBJECT_BASE_URL", "")

AUDIO_OUTPUT_DIR = os.getenv("AUDIO_OUTPUT_DIR", "./audio-output")
AUDIO_SAMPLE_RATE = int(os.getenv("AUDIO_SAMPLE_RATE", "16000"))
AUDIO_CHANNELS = int(os.getenv("AUDIO_CHANNELS", "1"))

WHISPER_MODEL_SIZE = os.getenv("WHISPER_MODEL_SIZE", "small")
WHISPER_DEVICE = os.getenv("WHISPER_DEVICE", "cpu")
WHISPER_COMPUTE_TYPE = os.getenv("WHISPER_COMPUTE_TYPE", "int8")

KEYWORD_TOP_K = int(os.getenv("KEYWORD_TOP_K", "15"))
MIN_TOKEN_LENGTH = int(os.getenv("MIN_TOKEN_LENGTH", "2"))
CALLBACK_MAX_RETRIES = int(os.getenv("CALLBACK_MAX_RETRIES", "3"))
CALLBACK_RETRY_SECONDS = float(os.getenv("CALLBACK_RETRY_SECONDS", "1.5"))
ANALYSIS_MOCK_MODE = os.getenv("ANALYSIS_MOCK_MODE", "false").lower() in {"1", "true", "yes", "on"}

_TOKEN_PATTERN = re.compile(r"[\w]{2,}")
_STOPWORDS = {
    "the",
    "and",
    "for",
    "with",
    "that",
    "this",
    "from",
    "have",
    "are",
    "was",
    "were",
    "you",
    "your",
}
_WHISPER_MODEL: WhisperModel | None = None
_MOCK_CALLBACK_LAST_PAYLOAD: dict[str, Any] | None = None


@dataclass
class TranscriptChunk:
    start: float
    end: float
    text: str


class AnalysisStartRequest(BaseModel):
    lectureResourceId: int = Field(..., ge=1)
    fileKey: str | None = None
    videoUrl: str | None = None
    s3Url: str | None = None
    callbackUrl: str = Field(..., min_length=8)
    requestId: str = Field(..., min_length=8)


class CallbackError(RuntimeError):
    pass


def _set_mock_callback_payload(payload: dict[str, Any]) -> None:
    global _MOCK_CALLBACK_LAST_PAYLOAD
    _MOCK_CALLBACK_LAST_PAYLOAD = payload


def _get_whisper_model() -> WhisperModel:
    global _WHISPER_MODEL
    if _WHISPER_MODEL is None:
        _WHISPER_MODEL = WhisperModel(
            WHISPER_MODEL_SIZE,
            device=WHISPER_DEVICE,
            compute_type=WHISPER_COMPUTE_TYPE,
        )
    return _WHISPER_MODEL


def _build_url_from_key(file_key: str) -> str:
    if not S3_OBJECT_BASE_URL:
        raise RuntimeError("received fileKey but S3_OBJECT_BASE_URL is empty")
    return f"{S3_OBJECT_BASE_URL.rstrip('/')}/{file_key.lstrip('/')}"


def resolve_video_source(req: AnalysisStartRequest) -> str:
    for candidate in [req.videoUrl, req.s3Url]:
        if isinstance(candidate, str) and candidate.strip():
            return candidate.strip()

    if req.fileKey and req.fileKey.strip():
        return _build_url_from_key(req.fileKey.strip())

    raise RuntimeError("no videoUrl/s3Url/fileKey provided")


def extract_audio_from_video(video_source: str) -> str:
    output_dir = Path(AUDIO_OUTPUT_DIR)
    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / f"{uuid4().hex}.wav"

    try:
        (
            ffmpeg.input(video_source)
            .output(
                str(output_path),
                acodec="pcm_s16le",
                ac=AUDIO_CHANNELS,
                ar=AUDIO_SAMPLE_RATE,
                vn=None,
            )
            .overwrite_output()
            .run(capture_stdout=True, capture_stderr=True)
        )
    except ffmpeg.Error as exc:
        stderr = exc.stderr.decode("utf-8", errors="ignore") if exc.stderr else ""
        raise RuntimeError(f"failed to extract audio with ffmpeg: {stderr}") from exc

    return str(output_path)


def transcribe_audio(audio_path: str) -> list[TranscriptChunk]:
    model = _get_whisper_model()
    segments, _ = model.transcribe(audio_path)

    chunks: list[TranscriptChunk] = []
    for seg in segments:
        text = seg.text.strip()
        if text:
            chunks.append(
                TranscriptChunk(
                    start=float(seg.start),
                    end=float(seg.end),
                    text=text,
                )
            )

    if not chunks:
        raise RuntimeError("no transcript chunks generated")
    return chunks


def _tokenize(text: str) -> list[str]:
    terms = [t.lower() for t in _TOKEN_PATTERN.findall(text)]
    return [t for t in terms if len(t) >= MIN_TOKEN_LENGTH and t not in _STOPWORDS]


def extract_keywords_tfidf(chunks: list[TranscriptChunk]) -> list[dict[str, float | str]]:
    tokenized_docs = [_tokenize(chunk.text) for chunk in chunks]
    tokenized_docs = [doc for doc in tokenized_docs if doc]
    if not tokenized_docs:
        return []

    doc_count = len(tokenized_docs)
    doc_freq = Counter()
    for doc in tokenized_docs:
        doc_freq.update(set(doc))

    scores = Counter()
    for doc in tokenized_docs:
        tf = Counter(doc)
        for term, freq in tf.items():
            idf = log((1 + doc_count) / (1 + doc_freq[term])) + 1
            scores[term] += float(freq) * idf

    if not scores:
        return []

    max_score = max(scores.values())
    if max_score <= 0:
        return []

    ranked = scores.most_common(KEYWORD_TOP_K)
    return [
        {"keyword": term, "importance": round(score / max_score, 4)}
        for term, score in ranked
    ]


def _callback_headers() -> dict[str, str]:
    return {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "X-ANALYSIS-SECRET": ANALYSIS_CALLBACK_SECRET,
    }


def _post_callback(callback_url: str, payload: dict[str, Any]) -> None:
    if callback_url.startswith("mock://"):
        _set_mock_callback_payload(payload)
        return

    last_error: Exception | None = None

    for attempt in range(1, CALLBACK_MAX_RETRIES + 1):
        try:
            response = requests.post(
                callback_url,
                json=payload,
                headers=_callback_headers(),
                timeout=(HTTP_CONNECT_TIMEOUT, HTTP_READ_TIMEOUT),
            )
            response.raise_for_status()
            return
        except requests.RequestException as exc:
            last_error = exc
            if attempt < CALLBACK_MAX_RETRIES:
                time.sleep(CALLBACK_RETRY_SECONDS * attempt)

    raise CallbackError(f"callback failed after retries: {last_error}")


def build_done_payload(
    lecture_resource_id: int,
    request_id: str,
    keywords: list[dict[str, float | str]],
) -> dict[str, Any]:
    return {
        "lectureResourceId": lecture_resource_id,
        "requestId": request_id,
        "status": "SUCCESS",
        "keywords": keywords,
        "errorMessage": None,
    }


def build_failed_payload(
    lecture_resource_id: int,
    request_id: str,
    message: str,
) -> dict[str, Any]:
    return {
        "lectureResourceId": lecture_resource_id,
        "requestId": request_id,
        "status": "FAILED",
        "keywords": None,
        "errorMessage": message,
    }


def build_mock_chunks() -> list[TranscriptChunk]:
    return [
        TranscriptChunk(start=0.0, end=7.2, text="This is a mock lecture opening about machine learning basics."),
        TranscriptChunk(start=7.2, end=14.5, text="The instructor explains supervised learning and model evaluation."),
        TranscriptChunk(start=14.5, end=22.0, text="Students review datasets, features, and practical workflow steps."),
    ]


def run_pipeline(req: AnalysisStartRequest) -> None:
    try:
        if ANALYSIS_MOCK_MODE:
            chunks = build_mock_chunks()
        else:
            video_source = resolve_video_source(req)
            audio_path = extract_audio_from_video(video_source)
            chunks = transcribe_audio(audio_path)

        keywords = extract_keywords_tfidf(chunks)
        payload = build_done_payload(req.lectureResourceId, req.requestId, keywords)
        _post_callback(req.callbackUrl, payload)
    except Exception as exc:  # pylint: disable=broad-except
        failed_payload = build_failed_payload(req.lectureResourceId, req.requestId, str(exc))
        _post_callback(req.callbackUrl, failed_payload)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/internal/analysis/start", status_code=202)
@app.post("/api/v1/analysis/start", status_code=202)
def start_analysis(req: AnalysisStartRequest, bg: BackgroundTasks) -> dict[str, Any]:
    if not ANALYSIS_CALLBACK_SECRET:
        raise HTTPException(status_code=500, detail="ANALYSIS_CALLBACK_SECRET is not configured")

    bg.add_task(run_pipeline, req)
    return {"status": "ACCEPTED", "lectureResourceId": req.lectureResourceId, "requestId": req.requestId}


@app.post("/api/v1/mock/callback")
def mock_callback(payload: dict[str, Any]) -> dict[str, str]:
    _set_mock_callback_payload(payload)
    return {"status": "ok"}


@app.get("/api/v1/mock/callback/last")
def get_last_mock_callback() -> dict[str, Any]:
    if _MOCK_CALLBACK_LAST_PAYLOAD is None:
        raise HTTPException(status_code=404, detail="no callback payload captured yet")
    return _MOCK_CALLBACK_LAST_PAYLOAD
