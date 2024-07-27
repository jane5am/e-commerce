FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY build/libs/product-service-0.0.1-SNAPSHOT.jar product-service.jar

ENV SPRING_APPLICATION_NAME=product-service

ENTRYPOINT ["java","-jar","/product-service.jar"]
