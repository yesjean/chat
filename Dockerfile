# Build Stage
FROM gradle:7.6-jdk17 as builder
WORKDIR /app
COPY . .
RUN gradle build  # Gradle 빌드 수행

# Run Stage
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY --from=builder /app/build/libs/chat-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
