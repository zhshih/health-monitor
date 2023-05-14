# Stage 1: Build the application
FROM gradle:latest as builder

# Set the working directory
WORKDIR /app

# Copy build files and dependencies
COPY build.gradle .
COPY src ./src

# Build the application
RUN gradle build --no-daemon -x test

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]