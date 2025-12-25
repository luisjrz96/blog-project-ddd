# syntax=docker/dockerfile:1.7

############################
# 1) Fetch OpenTelemetry Agent
############################
FROM alpine:3.20 AS otel-agent
ARG OTEL_JAVA_AGENT_VERSION=2.16.0
WORKDIR /otel
RUN apk add --no-cache curl ca-certificates \
 && curl -fsSL -o opentelemetry-javaagent.jar \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent.jar"

############################
# 2) Build (Gradle + JDK 21)
############################
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /workspace
ENV GRADLE_USER_HOME=/home/gradle/.gradle

COPY settings.gradle* build.gradle* gradle.properties ./
COPY gradle ./gradle
COPY --chmod=0755 gradlew ./gradlew

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon help

COPY . .

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon :blog-boot:bootJar -x test

############################
# 3) Runtime (JRE 21) + non-root + OTEL env vars + DB env vars
############################
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# non-root user
RUN useradd --system --home /app --shell /usr/sbin/nologin --uid 10001 appuser

# App + Agent
COPY --from=build /workspace/blog-boot/build/libs/*SNAPSHOT.jar /app/app.jar
COPY --from=otel-agent /otel/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

RUN set -eux; \
  cat > /app/entrypoint.sh <<'SH'
#!/usr/bin/env sh
set -e
exec java -jar /app/app.jar
SH
RUN chmod +x /app/entrypoint.sh \
 && chown -R appuser:appuser /app

USER appuser
EXPOSE 8080


ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

ENV OTEL_SERVICE_NAME="blog-api" \
    OTEL_TRACES_EXPORTER="otlp" \
    OTEL_LOGS_EXPORTER="otlp" \
    OTEL_METRICS_EXPORTER="none" \
    OTEL_EXPORTER_OTLP_TRACES_PROTOCOL="grpc" \
    OTEL_EXPORTER_OTLP_TRACES_ENDPOINT="http://otel-collector:4317" \
    OTEL_EXPORTER_OTLP_LOGS_PROTOCOL="http/protobuf" \
    OTEL_EXPORTER_OTLP_LOGS_ENDPOINT="http://otel-collector:4318/v1/logs"

ENTRYPOINT ["/app/entrypoint.sh"]
