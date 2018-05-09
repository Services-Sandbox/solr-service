# small linux distribution
FROM phusion/baseimage 

# install java
RUN apt-get -qq update
RUN apt-get -qq install default-jre -y
RUN apt-get -qq install default-jdk -y
RUN apt-get -qq install wget -y
RUN apt-get -qq install unzip -y
RUN apt-get -qq install lsof -y
RUN apt-get -qq install nano -y
RUN apt-get -qq -y install tar
RUN apt-get -qq -y install cron
RUN apt-get -qq -y install zip
RUN apt-get -qq -y install unzip
#RUN apt-get -qq -y install groovy


#echo "RUN apt-get -y update && curl -s get.sdkman.io | bash" >> Dockerfile
#echo 'RUN source "$HOME/.sdkman/bin/sdkman-init.sh"' >> Dockerfile
#echo 'RUN source ~/.profile' >> Dockerfile
#echo "RUN yes | sdk install groovy" >> Dockerfile

#temp
#RUN curl -s get.sdkman.io | bash && source "$HOME/.sdkman/bin/sdkman-init.sh"
#RUN source ~/.profile
#RUN yes | sdk install groovy
#RUN groovy -version


# installing java
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN mkdir /usr/java
RUN ln -s /usr/lib/jvm/java-1.8.0-openjdk-amd64 /usr/java/default
RUN mkdir /opt/solr/

# installing solr manually... user may need to be logged in as sudo
# see http://www.apache.org/dyn/closer.lua/lucene/solr/7.2.1 for other versions
# https://www.digitalocean.com/community/tutorials/how-to-install-solr-on-ubuntu-14-04
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
RUN wget http://httpd-mirror.sergal.org/apache/lucene/solr/7.3.0/solr-7.3.0.tgz --no-proxy -q && tar -xf solr-7.3.0.tgz && mv solr-7.3.0 solr && mv solr /opt/
RUN chmod -R 777 /opt/solr/server/solr/

# transfer the solr core into the solr engine
ADD tika-core /opt/solr/server/solr/
ADD elgg-core /opt/solr/server/solr/


# installing apache tika manually
# https://notesfromrex.wordpress.com/2015/02/09/install-apache-tika-on-debian/
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#RUN wget http://apache.forsale.plus/tika/tika-1.17-src.zip --no-proxy -q
#RUN unzip -qq tika-1.17-src.zip
#RUN mv tika-1.17 /opt/tika/


# install apache maven ... software project management tool
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#RUN wget http://mirror.csclub.uwaterloo.ca/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip --no-proxy -q
#RUN unzip -qq apache-maven-3.5.2-bin.zip
#RUN mv apache-maven-3.5.2 /opt/maven/
#WORKDIR /opt/tika/ 
#RUN /opt/maven/bin/mvn install -Dmaven.test.skip=true -q


RUN mkdir /usr/scripts
ADD groovy-scripts /usr/scripts/


# http://coder1.com/articles/easily-spin-solr-instances-docker
# clean up
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# expose the different ports required to access solr
EXPOSE 8983

#ENTRYPOINT ["docker/start.sh"]
CMD ["/opt/solr/bin/solr", "start", "-f", "-force"] 

