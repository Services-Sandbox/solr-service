import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// @TODO retrieve from user input for the URLs
// @TODO retrieve a list of types and subtypes from the application side

def subtypes = ["file", "comment", "blog", "thewire", "album", "image", "groupforumtopic", "page_top", "event_calendar", "hjforumtopic", "bookmarks", "poll", "idea", "page", "question"]

// retrieve the json text from the api (elgg)
//URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.entity_list&type=object&subtype=blog")

for (subtype in subtypes) {

	URL apiURL = new URL("http://192.168.245.130/gcconnex/services/api/rest/json/?method=get.entity_list&type=object&subtype=$subtype")

	def slurper = new JsonSlurper()
	def api_response = slurper.parseText(apiURL.text)

	def results = api_response.result
	for (result in results) {

		def title = result.title
		def description = result.description
		// build the json string and curl post to solr
		def json_string = new JsonBuilder(["guid": result.guid, "title": "$title", "description": "$description", "type": "$result.type", "subtype": "$result.subtype", "access_id": result.access_id])
		def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.245.130:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

		println json_string.toString()
		println process
		println "----------------------"
		//http://192.168.1.18:8983/solr/elgg-core/select?q=title:please&q=description:please
	}
}


