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

# Install curl
RUN apt-get update && apt-get install -y curl

# Set the working directory
WORKDIR /app

EXPOSE 8071

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]