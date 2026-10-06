# Stage 1: Build using Maven and Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy Maven wrapper and POM
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy project source code
COPY src src

# Package production executable JAR without running tests during image build
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal, secure JRE runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Add a non-root system user for security
RUN groupadd -r railnova && useradd -r -g railnova railnova

# Copy artifact from build stage
COPY --from=build /app/target/railnova-backend-1.0.0.jar app.jar
RUN chown -R railnova:railnova /app

USER railnova

# Port configuration (Render passes PORT automatically)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
