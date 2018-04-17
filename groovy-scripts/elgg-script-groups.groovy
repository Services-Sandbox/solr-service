
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// retrieve all groups
URL apiURL = new URL("http://192.168.245.130/gcconnex/services/api/rest/json/?method=get.group_list")

def slurper = new JsonSlurper()
def api_response = slurper.parseText(apiURL.text)

def results = api_response.result
for (result in results) {

	def name = result.name
	def description = result.description
	// build the json string and curl post to solr
	def json_string = new JsonBuilder(["guid": result.guid, "name": "$name", "description": "$description", "type": "$result.type", "access_id": result.access_id])
	def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.245.130:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

	println json_string.toString()
	println process
	println "----------------------"
	//http://192.168.1.18:8983/solr/elgg-core/select?q=title:please&q=description:please
}
