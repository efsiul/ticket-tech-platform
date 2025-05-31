FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/discovery-service-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties application.properties

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/Guayaquil /etc/localtime \
    && echo "America/Guayaquil" > /etc/timezone

EXPOSE 8261

CMD ["java", "-jar", "app.jar"]
