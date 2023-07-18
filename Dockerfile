# Choose a base Java image
FROM openjdk:19-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file of your application into the container
COPY target/listvideo-api-0.0.1-SNAPSHOT.jar /app/app.jar

# Define the port that your application exposes (if needed)
EXPOSE 8080

# Command to execute your Spring Boot application
CMD ["java", "-jar", "/app/app.jar"]