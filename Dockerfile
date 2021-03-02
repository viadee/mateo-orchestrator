FROM openjdk:15-alpine

RUN apk add --no-cache bash && mkdir /opt/mateo-orchestrator
COPY target/mateo-orchestrator-0.0.2-SNAPSHOT.jar /opt/mateo-orchestrator/
COPY target/classes/application.yml /opt/mateo-orchestrator/

EXPOSE 8082

WORKDIR /opt/mateo-orchestrator
CMD  [ "java", "-Dspring.profiles.active=default", "-jar", "mateo-orchestrator-0.0.1-SNAPSHOT.jar" ]
