FROM java:8

# preserve Java 8  from the maven install.
RUN mv /etc/alternatives/java /etc/alternatives/java8
RUN apt-get update -y && apt-get install maven -y && apt-get install git

# Restore Java 8
RUN mv -f /etc/alternatives/java8 /etc/alternatives/java
RUN ls -l /usr/bin/java && java -version

RUN mkdir -p /backend
WORKDIR /backend

COPY . /backend
RUN ls -l /backend/service/target

ENV PORT 8081
EXPOSE 8081

