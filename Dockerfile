# small linux distribution
FROM phusion/baseimage 

# install java
RUN apt-get update
RUN apt-get install default-jre -y
RUN apt-get install default-jdk -y
RUN apt-get install wget -y
RUN apt-get install unzip -y
RUN apt-get install lsof -y

# installing solr manually... user may need to be logged in as sudo
# see http://www.apache.org/dyn/closer.lua/lucene/solr/7.2.1 for other versions
# https://www.digitalocean.com/community/tutorials/how-to-install-solr-on-ubuntu-14-04
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN mkdir /usr/java
RUN ln -s /usr/lib/jvm/java-1.8.0-openjdk-amd64 /usr/java/default
ADD solr /opt/solr


# installing apache tika manually
# https://notesfromrex.wordpress.com/2015/02/09/install-apache-tika-on-debian/
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#ADD tika /opt/tika


# install apache maven ... software project management tool
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#ADD maven /opt/maven

#http://coder1.com/articles/easily-spin-solr-instances-docker
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# expose the different ports required to access solr
EXPOSE 8983

#ENTRYPOINT ["docker/start.sh"]
CMD ["/opt/solr/bin/solr", "start", "-f", "-force"]
