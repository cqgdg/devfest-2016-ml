FROM openjdk:8u111-jre-alpine

ADD target/devfest-2016-ml.war /opt/app/devfest-2016-ml.war
EXPOSE 8080
CMD java -jar /opt/app/devfest-2016-ml.war > /var/log/devfest-2016-ml.log
