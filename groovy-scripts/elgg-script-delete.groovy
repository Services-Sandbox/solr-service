

def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.1.18:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

println process
println "----------------------"