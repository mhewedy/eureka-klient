package eureka

import helpers.json.Parser
import helpers.post

const val apiBaseUrl = "http://localhost:8761/eureka"

interface EurekaApi {
    fun register(app: String, instanceInfo: InstanceInfo)
}

class EurekaApiImpl : EurekaApi {

    override fun register(app: String, instanceInfo: InstanceInfo) {

        post("$apiBaseUrl/apps/$app", instanceInfo) {
            println(responseCode)
            Parser(responseText).use {
                println(it.parse())
            }
        }
    }
}

fun main() {
    val eurekaApi = EurekaApiImpl()

    val myIP = "192.168.1.10"
    val appName = "eureka-klient"

    eurekaApi.register(
        appName, InstanceInfo(
            Instance(
                app = appName,
                ipAddr = myIP,
                hostName = myIP,
                instanceId = myIP,
                status = StatusType.UP,
                overriddenStatus = StatusType.UNKNOWN,
                port = Port(8080, "true"),
                securePort = Port(443, "false"),
                countryId = 1,
                dataCenterInfo = DataCenterInfo(
                    DcNameType.MY_OWN,
                    "com.netflix.appinfo.InstanceInfo\$DefaultDataCenterInfo"
                )
            )
        )
    )
}