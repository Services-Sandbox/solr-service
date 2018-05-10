import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.xml.*
// @TODO retrieve from user input for the URLs
// @TODO retrieve a list of types and subtypes from the application side
 
// retrieve the json text from the api (elgg)
//URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.entity_list&type=object&subtype=blog")



URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.list_of_deleted_records")

def slurper = new JsonSlurper()
def api_response = slurper.parseText(apiURL.text)

def list_of_documents_to_delete = []

def results = api_response.result
for (result in results) {

	def xml_string = "<delete><id>$result.guid</id></delete>"
	def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/xml\" --data-binary '" + xml_string + "' http://192.168.1.18:8983/solr/elgg-core/update?commit=true" ].execute().text

	println xml_string.toString()
	println process

	def responses = new XmlSlurper().parseText(process)

	def response_text = responses.lst.find { it.'@name' == 'error'}.str.text()
	if (response_text == '') {
		println "remove [$result.guid] $response_text "
		list_of_documents_to_delete.push(result.guid)
	}

	println "----------------------"
	//http://192.168.1.18:8983/solr/elgg-core/select?q=title:please&q=description:please 
}

println "--- clean up --- >>> " + list_of_documents_to_delete



 