# Usa una imagen base de OpenJDK
FROM openjdk:17

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor
COPY target/language-service-1.0-SNAPSHOT.jar app.jar

# Expone el puerto que tu aplicación va a usar
EXPOSE 8021

# Define el comando para ejecutar tu aplicación
CMD ["java", "-jar", "app.jar"]
