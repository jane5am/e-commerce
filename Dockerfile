FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY build/libs/e-commerce-0.0.1-SNAPSHOT.jar discovery-service.jar

ENTRYPOINT ["java","-jar","/discovery-service.jar"]
