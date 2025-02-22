# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/tp-foyer-5.0.0.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]