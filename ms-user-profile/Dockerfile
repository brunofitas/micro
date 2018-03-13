FROM openjdk:8u121-jdk-alpine

MAINTAINER Bruno Fitas <brunofitas@gmail.com>

ADD target/scala-2.12/*jar /app/service.jar

RUN apk --update add wget bash

RUN wget https://raw.githubusercontent.com/eficode/wait-for/master/wait-for -O /wait-for

RUN chmod +x /wait-for

EXPOSE 8080