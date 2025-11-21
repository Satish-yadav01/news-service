# ============================
# 1. Build Stage
# ============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies (takes advantage of caching)
RUN mvn dependency:go-offline -B

# Copy the rest of the project
COPY src ./src

# Build the jar file
RUN mvn clean package -DskipTests

# ============================
# 2. Run Stage
# ============================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar news-service.jar

# Expose application port (optional but recommended)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "news-service.jar"]
