#See: https://github.com/jboss/dockerfiles

FROM fedora

# Setup system packages
RUN yum -y install java-1.7.0-openjdk-devel
RUN yum clean all

# Setup Kornell
RUN curl -o /opt/kornell-api-opt.zip https://s3-sa-east-1.amazonaws.com/dist-sa-east-1.craftware.com/kornell-api-opt.zip
RUN gunzip /kornell-api-opt.zip
RUN rm /kornell-api-opt.zip

# Set WildFly user
RUN groupadd -r wildfly -g 433 && useradd -u 431 -r -g wildfly -d /opt/wildfly -s /sbin/nologin -c "WildFly user" wildfly
RUN chown -R wildfly:wildfly /opt/wildfly/*

# Expose the ports we're interested in
EXPOSE 8080 9990

USER wildfly