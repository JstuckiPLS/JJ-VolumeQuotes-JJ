FROM tomcat:9.0.37-jdk8
MAINTAINER cburris@plslogistics.com

RUN rm -rf /usr/local/tomcat/webapps/*

ENV CATALINA_HOME /usr/local/tomcat/
COPY dist/wars/ /usr/local/tomcat/webapps/
COPY dist/conf/ /usr/local/tomcat/conf/

CMD ["catalina.sh", "run"]