import com.urbancode.air.AirPluginTool
import HttpHelper
import static groovyx.net.http.Method.*




/* This gets us the plugin tool helper. 
 * This assumes that args[0] is input props file and args[1] is output props file.
 * By default, this is true. If your plugin.xml looks like the example. 
 * Any arguments you wish to pass from the plugin.xml to this script that you don't want to 
 * pass through the step properties can be accessed using this argument syntax
 */
final airTool = new AirPluginTool(args[0], args[1])

/* Here we call getStepProperties() to get a Properties object that contains the step properties
 * provided by the user. 
 */
final def props = airTool.getStepProperties()

/* This is how you retrieve properties from the object. You provide the "name" attribute of the 
 * <property> element.
 * 
 */

//Obter valores das propriedades do passo
final def processId = props['process_id']
final def resourceId = props['resource_id']
final def executionStringList = props['execution_list']

//Utilizar HttpHelper para executar um processo genérico em lote

def executionList = executionStringList.split("\n")
def executionCount = 0

executionList.each	{
	def processRequestUrl = System.getenv('AH_WEB_URL')+"/cli/processRequest"
	def payload = """{
    "processId" : "${processId}",
    "resources" : ["${resourceId}"],
    "properties": ${it}
}"""
	HttpHelper helper = new HttpHelper(processRequestUrl,POST,payload)
	helper.setCredenciais('PasswordIsAuthToken',System.getenv('DS_AUTH_TOKEN'))
	def response = helper.getResponse()
	if (response) {
		executionCount++
		println "Processo Acionado #${executionCount}: ${response}"
	}
}