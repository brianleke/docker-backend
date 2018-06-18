FROM java:8

# preserve Java 8  from the maven install. This is because mvn installs java 7 so this is necessary to maintain java 8
RUN mv /etc/alternatives/java /etc/alternatives/java8
RUN apt-get update -y && apt-get install maven -y && apt-get install git &&  apt-get install -y vim

# Restore Java 8
RUN mv -f /etc/alternatives/java8 /etc/alternatives/java

RUN mkdir -p /backend
WORKDIR /backend

#Clone the backend service repository
RUN git clone https://github.com/brianleke/docker-backend.git

#Move the cloned backend from its current folder to the service folder
RUN mv docker-backend/ service/

#Change work directory to the service folder
WORKDIR /backend/service

#Run the maven package command to build the packaged artefact for the application
RUN mvn package

#Change working directory back to the root folder to run the application
WORKDIR /backend

#Expose the port 8081 so that it is accessible from the host machine
ENV PORT 8081
EXPOSE 8081

