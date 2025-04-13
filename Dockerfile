# 🔨 Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml separately to leverage Docker cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Optional: pre-fetch spring-expression dependency (if needed)
RUN mvn dependency:get -Dartifact=org.springframework:spring-expression:6.1.13 || true

# Package the application
RUN mvn clean package -DskipTests

# 🚀 Runtime Stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Optionally set Spring profile (only if your app supports it)
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the app port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
