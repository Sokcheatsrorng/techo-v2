# Build stage - Use Java 17 JDK for building to match your project's requirement
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files first (for better caching)
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Make gradlew executable and run the build
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x test

# Final stage - Use official Temurin JRE (smaller than JDK); stick with 21 if preferred, or change to 17-jre-alpine
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage (adjust path if your JAR name differs, e.g., /app/build/libs/your-app-0.0.1-SNAPSHOT.jar)
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port (matches your compose file)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]