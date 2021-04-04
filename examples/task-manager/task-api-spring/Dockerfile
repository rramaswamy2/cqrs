


FROM openjdk:alpine

RUN mkdir -p /opt/taskmanager/api
RUN mkdir -p /opt/taskmanager/config

ENV JAVA_OPTS=""
#ENV LOGGING_CONFIG="/opt/taskmanager/config/log4j2.xml"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /opt/taskmanager/api/app.jar" ]

EXPOSE 8000

ARG JAR_VERSION=0.1.0-SNAPSHOT

#COPY log4j2.xml /opt/taskmanager/config
COPY build/libs/task-api-spring-${JAR_VERSION}.jar /opt/taskmanager/api/app.jar

RUN touch /opt/taskmanager/api/app.jar
