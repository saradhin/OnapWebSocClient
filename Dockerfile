#openjdk 11 base image
FROM openjdk:11-jdk-slim

COPY target /Home/app/OnapWebSocket/target/.

COPY start.sh /Home/app/OnapWebSocket/start.sh


# create volume on machine to access log files outside container 
#VOLUME /Home/app/OnapWebSocket/log 

WORKDIR /Home/app/OnapWebSocket

RUN chmod 755 start.sh

ENTRYPOINT ["/Home/app/OnapWebSocket/start.sh"]
