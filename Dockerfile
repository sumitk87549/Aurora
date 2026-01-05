FROM openjdk:21-jdk

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "target/AuroraFlames-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
