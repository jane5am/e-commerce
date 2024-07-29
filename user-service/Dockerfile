FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY build/libs/user-service-0.0.1-SNAPSHOT.jar user-service.jar

ENV SPRING_APPLICATION_NAME=user-service

ENTRYPOINT ["java","-jar","/user-service.jar"]
