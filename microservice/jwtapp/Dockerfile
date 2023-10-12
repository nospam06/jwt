FROM openjdk:17

COPY build/libs/jwtapp-*.jar /opt/app.jar

RUN chmod +x /opt/app.jar
EXPOSE 8080
ENTRYPOINT java -jar /opt/app.jar