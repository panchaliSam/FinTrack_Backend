# Use OpenJDK 21 for building the app (build stage)
FROM openjdk:21-slim as build

# Set the working directory to /app
WORKDIR /app

# Install Maven in the build stage
RUN apt-get update && apt-get install -y maven

# Copy the pom.xml and download the dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src /app/src

# Build the app using Maven
RUN mvn clean package -DskipTests

# Use OpenJDK 21 to run the application (final stage)
FROM openjdk:21-slim

# Copy the packaged jar file from the build stage
COPY --from=build /app/target/*.jar app.jar


# Expose the port your app runs on
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
