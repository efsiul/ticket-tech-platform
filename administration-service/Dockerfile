# Uses an OpenJDK image for Java
FROM openjdk:17

# Sets the working directory
WORKDIR /app

# Copy the JAR file built in the container
COPY target/administration-service-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que la aplicación se ejecutará
EXPOSE 8022

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
