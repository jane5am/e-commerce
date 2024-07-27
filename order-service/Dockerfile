FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY build/libs/order-service-0.0.1-SNAPSHOT.jar order-service.jar

ENV SPRING_APPLICATION_NAME=order-service

ENTRYPOINT ["java","-jar","/order-service.jar"]
