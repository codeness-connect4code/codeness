FROM openjdk:17
WORKDIR /app
COPY build/libs/codeness-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/firebase-codeness-key.json /app/firebase-codeness-key.json
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
