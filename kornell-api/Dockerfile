FROM jboss/base-jdk:8
USER root
RUN mkdir -p /opt/kornell-api
ADD target/kornell-api-swarm.jar /opt/kornell-api/kornell-api-swarm.jar
ADD http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.38/mysql-connector-java-5.1.38.jar /opt/kornell-api/mysql-connector-java-5.1.38.jar 
USER jboss
EXPOSE 8080
CMD java -cp /opt/kornell-api/mysql-connector-java-5.1.38.jar  -jar /opt/kornell-api/kornell-api-swarm.jar  