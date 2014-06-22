#See: https://github.com/jboss/dockerfiles

FROM fedora

# Setup Kornell
RUN curl -o /tmp/kornell-api-opt.tar.gz https://s3-sa-east-1.amazonaws.com/dist-sa-east-1.craftware.com/kornell-api-opt.tar.gz
RUN tar zxf /tmp/kornell-api-opt.tar.gz --warning=no-unknown-keyword -C /opt
RUN rm /tmp/kornell-api-opt.tar.gz

# Set WildFly user
RUN groupadd -r wildfly -g 433 && useradd -u 431 -r -g wildfly -d /opt/wildfly -s /sbin/nologin -c "WildFly user" wildfly
RUN chown -R wildfly:wildfly /opt/wildfly/*
