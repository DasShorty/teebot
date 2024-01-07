FROM openjdk:17

COPY teebot.jar /home

WORKDIR /home

ENTRYPOINT java -jar teebot.jar