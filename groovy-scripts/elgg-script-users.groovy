
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// retrieve all users
URL apiURL = new URL("http://192.168.245.130/gcconnex/services/api/rest/json/?method=get.user_list")

def slurper = new JsonSlurper()
def api_response = slurper.parseText(apiURL.text)

def results = api_response.result
for (result in results) {

	def name = result.name
	// build the json string and curl post to solr
	def json_string = new JsonBuilder([
		"guid": result.guid, 
		"name": "$name", 
		"type": "$result.type", 
		"username": "$result.username", 
		"email": "$result.email",
		"date_created": "$result.date_created",
		"date_modified": "$result.date_modified"
	])

	def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.245.130:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

	println json_string.toString()
	println process
	println "----------------------"
	//http://192.168.1.18:8983/solr/elgg-core/select?q=title:please&q=description:please
}