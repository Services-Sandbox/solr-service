FROM solr
ENV AUTHOR=cyu

# creating the core (manually)
#USER root
#COPY tika-core/ /opt/solr/server/solr/
#RUN chmod -R 777 /opt/solr/server/solr/

# expose the different ports required to access solr
EXPOSE 8983
