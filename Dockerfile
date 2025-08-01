# Builder stage
FROM gradle:8.4-jdk17-alpine AS builder
WORKDIR /app

# Copy only necessary files for dependency resolution first (better caching)
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# Download dependencies (cached layer if no build file changes)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build application
RUN gradle build --no-daemon -x test

# Final stage
FROM openjdk:17-alpine
WORKDIR /app

# Copy the JAR file
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Create directories and set permissions
RUN mkdir -p /app/media /app/keys && \
    chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]