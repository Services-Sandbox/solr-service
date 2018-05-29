import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.xml.*

// @TODO retrieve from user input for the URLs
// @TODO retrieve a list of types and subtypes from the application side
// @TODO error logging
// @TODO documentations
// http://192.168.1.18/gcconnex/services/api/rest/json/?method=delete.updated_index_list&guids[]=test&guids[]=2&test=hello

/// PART 1: retrieve a list of all the deleted content from
/// ===================================================================
URL apiURL = new URL("http://192.168.1.18/gcconnex/services/api/rest/json/?method=get.list_of_deleted_records&api_key=4d09e3b4dd0276e9308cf88740a34d62923a55d9")

def slurper = new JsonSlurper()
def api_response = slurper.parseText(apiURL.text)

def list_of_documents_to_delete = []
def guids = ""
def results = api_response.result
def is_first = true

for (result in results) {

	def xml_string = "<delete><id>$result.guid</id></delete>"
	def process = [ 'bash', '-c', "curl -v -k -X POST -H \"Content-Type: application/xml\" --data-binary '" + xml_string + "' http://192.168.1.18:8983/solr/elgg-core/update?commit=true" ].execute().text

	println xml_string.toString()
	println process

	def responses = new XmlSlurper().parseText(process)
	def response_text = responses.lst.find { it.'@name' == 'error'}.str.text()
	
	if (response_text == '') {
		list_of_documents_to_delete.push(result.guid)
		guids = (is_first) ? guids + "guids[]=$result.guid" : guids + "&guids[]=$result.guid"
		is_first = false
	}

}


/// PART 2: clean up the database after the deletion is successful
/// ===================================================================
def process = [ 'bash', '-c', "curl -X POST -d '" + guids + "' http://192.168.1.18/gcconnex/services/api/rest/json/?method=delete.updated_index_list" ].execute().text


println process

 