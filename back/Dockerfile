FROM openjdk:11-slim

COPY target/springvue-*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
