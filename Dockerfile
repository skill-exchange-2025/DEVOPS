# Build stage
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy pom.xml for dependency resolution
COPY pom.xml .

# Add the missing spring-expression dependency
RUN mvn dependency:get -Dartifact=org.springframework:spring-expression:6.1.13

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the application port
EXPOSE 8089

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]