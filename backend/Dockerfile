FROM eclipse-temurin:24.0.1_9-jdk-alpine-3.21
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar","app.jar"]