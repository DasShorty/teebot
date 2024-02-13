FROM openjdk:21

COPY teebot.jar /home

WORKDIR /home

ENTRYPOINT java -jar teebot.jar