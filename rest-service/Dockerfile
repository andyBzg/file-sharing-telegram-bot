FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/app.jar
WORKDIR /app
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
