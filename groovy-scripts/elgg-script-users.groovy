
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

def end_loop = false
def offset = 0

while (!end_loop) {

	// retrieve all users
	URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.user_list&offset=$offset&api_key=4d09e3b4dd0276e9308cf88740a34d62923a55d9")

	def slurper = new JsonSlurper()
	def api_response = slurper.parseText(apiURL.text)
	def results = api_response.result

	if (results == null) {
		end_loop = true
		break
	}

	for (result in results) {

		if (result.guid == null) continue

		def name = result.name
		def text_en = "$name $result.email $result.username" 
		def text_fr = "$name $result.email $result.username"

		// build the json string and curl post to solr
		def json_string = new JsonBuilder([
			"guid": result.guid, 
			"name": "$name", 
			"text_en": "$text_en",
			"text_fr": "$text_fr",
			"type": "$result.type", 
			"username": "$result.username", 
			"email": "$result.email",
			"date_created": "$result.date_created",
			"date_modified": "$result.date_modified"
		])

		def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.1.18:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

		println json_string.toString()
		println process
		println "----------------------"
		//http://192.168.1.18:8983/solr/elgg-core/select?q=title:please&q=description:please
	}
	offset = offset + 10
}