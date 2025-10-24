# syntax=docker/dockerfile:1.7

# Stage 1: (optional) fetch OpenTelemetry agent
FROM alpine:3.20 AS otel-agent
ARG OTEL_JAVA_AGENT_VERSION=2.16.0
WORKDIR /otel
RUN apk add --no-cache curl \
 && curl -fsSL -o opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent.jar

# Stage 2: Build with Gradle (JDK 21)
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /workspace

# 1) Copy only build scripts first to leverage layer caching of dependencies
COPY settings.gradle* build.gradle* gradle.properties ./
COPY gradle ./gradle
# 2) Pre-fetch dependencies using BuildKit cache mount (no source code yet)
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle --no-daemon help
# 3) Now copy the rest of the project sources
COPY . .

# 4) Build the bootable JAR for the blog-boot module
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle :blog-boot:bootJar --no-daemon -x test

# Stage 3: Runtime (JRE 21)
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Copy app jar from the builder stage
COPY --from=build /workspace/blog-boot/build/libs/blog-boot-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy OTel javaagent from the fetch stage
COPY --from=otel-agent /otel/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# Non-root user
RUN useradd -r -s
