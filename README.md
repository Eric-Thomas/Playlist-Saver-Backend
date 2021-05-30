# Playlist-Saver-Backend
Backend api for spotify playlist saver app

## How to run locally

 * Build jar file with maven from base directory
 `mvn clean package`
 
 * Edit Dockerfile to the following
 `
 	FROM openjdk:15-jdk-alpine
	RUN addgroup -S spring && adduser -S spring -G spring
	USER spring:spring
	ARG JAR_FILE=target/*.jar
	COPY ${JAR_FILE} app.jar
	COPY credentials credentials
	ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/app.jar"]
`

Note: Must have aws credentials file for your account. Important changes here are active profiles to dev and COPY credentials credentials
Also make sure permissions on credentials are set to read so it is able to copy with command
`chmod 444 credentials`


* Build docker image from base directory

`docker build -t {REPOSITORY-NAME}/playlist-saver .`

* Run docker container
`docker run -p 8080:8080 {REPOSITORY-NAME}/playlist-saver1`

