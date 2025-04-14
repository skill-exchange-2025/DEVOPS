# Build stage: Use Maven to build the application
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy pom.xml to resolve dependencies
COPY pom.xml .

# Add missing spring-expression dependency (if needed)
RUN mvn dependency:get -Dartifact=org.springframework:spring-expression:6.1.13

# Download all project dependencies (offline mode)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Runtime stage: Use Amazon Corretto JDK 17 for running the application
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables (e.g., Spring profile)
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the application port (make it customizable by ENV variable)
EXPOSE 8089

# Run the application (make it more flexible with CMD)
CMD ["java", "-jar", "app.jar"]
