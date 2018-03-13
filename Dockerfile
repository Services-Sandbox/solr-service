# small linux distribution
FROM phusion/baseimage 

# install java
RUN apt-get update
RUN apt-get install default-jre -y
RUN apt-get install default-jdk -y
RUN apt-get install wget -y
RUN apt-get install unzip -y
RUN apt-get install lsof -y

# installing java
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN mkdir /usr/java
RUN ln -s /usr/lib/jvm/java-1.8.0-openjdk-amd64 /usr/java/default


# installing solr manually... user may need to be logged in as sudo
# see http://www.apache.org/dyn/closer.lua/lucene/solr/7.2.1 for other versions
# https://www.digitalocean.com/community/tutorials/how-to-install-solr-on-ubuntu-14-04
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN wget http://httpd-mirror.sergal.org/apache/lucene/solr/7.2.1/solr-7.2.1.tgz --no-proxy -q
RUN tar -xvf solr-7.2.1.tgz
RUN mv solr-7.2.1/ /opt/solr/

# transfer the solr core into the solr engine
ADD tika-core /opt/solr/server/solr/


# installing apache tika manually
# https://notesfromrex.wordpress.com/2015/02/09/install-apache-tika-on-debian/
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN wget http://apache.forsale.plus/tika/tika-1.17-src.zip --no-proxy -q
RUN unzip -qq tika-1.17-src.zip
RUN mv tika-1.17 /opt/tika/


# install apache maven ... software project management tool
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN wget http://mirror.csclub.uwaterloo.ca/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip --no-proxy -q
RUN unzip -qq apache-maven-3.5.2-bin.zip
RUN mv apache-maven-3.5.2 /opt/maven/
WORKDIR /opt/tika/ 
RUN /opt/maven/bin/mvn install


# http://coder1.com/articles/easily-spin-solr-instances-docker
# clean up
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# expose the different ports required to access solr
EXPOSE 8983

#ENTRYPOINT ["docker/start.sh"]
CMD ["/opt/solr/bin/solr", "start", "-f", "-force"] 

