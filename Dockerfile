# ---- Stage 1: Build ----
FROM gradle:8-jdk17

# Set working directory
WORKDIR /app

# Copy project files
COPY --chown=gradle:gradle . .

# Build application (creates .jar file in build/libs)
RUN gradle clean build -x test

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from previous stage
COPY --from=0 /app/build/libs/*-all.jar app.jar

# Expose application port (adjust if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
