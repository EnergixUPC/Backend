# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime - Use Eclipse Temurin JRE 25
FROM eclipse-temurin:25-jre

WORKDIR /app

# --- NUEVO: Configurar la zona horaria a Lima, Perú ---
ENV TZ=America/Lima
# Opcional pero recomendado: configurar el sistema operativo del contenedor
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Copy the built JAR from the build stage
COPY --from=build /app/target/backend-sems-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]