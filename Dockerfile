# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-23 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime - Use Oracle JDK 25
FROM container-registry.oracle.com/java/jdk:25

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/backend-sems-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]