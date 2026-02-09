# Dockerfile for Slack Clone Backend

FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties ./

# Copy all module build files
COPY shared-module/build.gradle shared-module/
COPY identity-module/build.gradle identity-module/
COPY chat-module/build.gradle chat-module/
COPY app/build.gradle app/

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY shared-module/src shared-module/src
COPY identity-module/src identity-module/src
COPY chat-module/src chat-module/src
COPY app/src app/src

# Build application
RUN ./gradlew :app:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
