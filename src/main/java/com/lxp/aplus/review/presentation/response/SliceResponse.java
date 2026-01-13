package com.lxp.aplus.review.presentation.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(
        List<T> content,
        int page,
        int size,
        Integer totalCount,
        boolean hasNext
) {
    public static <T> SliceResponse<T> of(Slice<T> slice, Integer totalCount) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                totalCount,
                slice.hasNext()
        );
    }
}
