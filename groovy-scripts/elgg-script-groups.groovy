
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// retrieve all groups
URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.group_list&offset=20")

def slurper = new JsonSlurper()
def api_response = slurper.parseText(apiURL.text)

def results = api_response.result
for (result in results) {

	println "result: " + result.guid

	if (result.guid == null) continue

	def name = result.name
	def description = result.description

	def name_en = (name.en).replaceAll("\"", " ")
	def name_fr = (name.fr).replaceAll("\"", " ")
	def description_en = (description.en).replaceAll("\"", " ")
	def description_fr = (description.fr).replaceAll("\"", " ")

	def text_en = "$name_en $description_en" 
	def text_fr = "$name_fr $description_fr"

	// build the json string and curl post to solr
	def json_string = new JsonBuilder([
		"guid": result.guid, 
		"name_en": "$name_en", 
		"name_fr": "$name_fr", 
		"description_en": "$description_en",
		"description_fr": "$description_fr",
		"text_en": "$text_en",
		"text_fr": "$text_fr",
		"type": "$result.type", 
		"access_id": result.access_id,
		"date_created": "$result.date_created",
		"date_modified": "$result.date_modified",
		"url": "$result.url"
	])

	def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.1.18:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

	println json_string.toString()
	println process
	println "----------------------"
	//http://192.168.1.18:8983/solr/elgg-core/select?q=title:please&q=description:please
	
}
