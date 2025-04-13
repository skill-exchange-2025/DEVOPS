# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/*.jar app.jar

# Expose the application port (8080)
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
