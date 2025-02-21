# Use a lightweight OpenJDK image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the JAR file from the target directory (Ensure you've built your app first)
COPY target/*.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE 3000

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
