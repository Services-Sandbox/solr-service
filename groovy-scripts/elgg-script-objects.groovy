import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// @TODO retrieve from user input for the URLs


def subtypes = ["file", "comment", "blog", "thewire", "album", "image", "groupforumtopic", "page_top", "event_calendar", "hjforumtopic", "bookmarks", "poll", "idea", "page", "question"]


// retrieve the json text from the api (elgg)
for (subtype in subtypes) {

	def end_loop = false
	def offset = 0
	// the list displays 10 entities each time will need to "paginate"
	while (!end_loop) {

		URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.entity_list&type=object&subtype=$subtype&offset=$offset&api_key=4d09e3b4dd0276e9308cf88740a34d62923a55d9")
		println "offset: $offset" 

		def slurper = new JsonSlurper()
		def api_response = slurper.parseText(apiURL.text)
		def results = api_response.result

		if (api_response.result == null) {
			end_loop = true
			break
		}

		for (result in results) {
			
			if (result == null) continue 

			def title = result.title
			def description = result.description

			def title_en = ""
			def title_fr = ""
			def description_en = ""
			def description_fr = ""

			// assuming not all entities have consistent english & french titles and descriptions
			try {
					title_en = (title.en).replaceAll("\"", " ")
					title_fr = (title.fr).replaceAll("\"", " ")
				} catch (MissingPropertyException mpe) {
					title_en = title
					title_fr = title
				}

			try {
					description_en = ((description.en).toString()).replaceAll("\"", " ")
					description_fr = ((description.fr).toString()).replaceAll("\"", " ")
				} catch (MissingPropertyException mpe) {
					description_en = description
					description_fr = description
				}


			def text_en = "$title_en $description_en" 
			def text_fr = "$title_fr $description_fr"

			// build the json string and curl post to solr
			def json_string = new JsonBuilder([
				"guid": result.guid, 
				"title_en": "$title_en", 
				"title_fr": "$title_fr", 
				"description_en": "$description_en",
				"description_fr": "$description_fr", 
				"text_en": "$text_en",
				"text_fr": "$text_fr",
				"type":	"$result.type", 
				"subtype": "$result.subtype", 
				"access_id": result.access_id,
				"date_created": "$result.date_created",
				"date_modified": "$result.date_modified",
				"url" : "$result.url"
			])

			def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/json\" --data-binary '" + json_string.toString() + "' http://192.168.1.18:8983/solr/elgg-core/update/json/docs?commit=true" ].execute().text

		}
		offset = offset + 10
	}
}


