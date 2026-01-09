FROM gradle:9.2.1-jdk17 AS build
WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY . .

RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
