FROM openjdk:15-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY credentials credentials
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/app.jar"]