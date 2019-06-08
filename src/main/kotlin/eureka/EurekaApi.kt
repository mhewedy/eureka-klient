package eureka

import helpers.json.Parser
import helpers.post

const val apiBaseUrl = "http://localhost:8761/eureka"

interface EurekaApi {
    fun register(app: String, instanceWrapper: Wrapper)
}

class EurekaApiImpl : EurekaApi {

    override fun register(app: String, instanceWrapper: Wrapper) {

        post("$apiBaseUrl/apps/$app", instanceWrapper) {
            println(responseCode)
            println(responseText)
            Parser(responseText).use {
                println(it.parse())
            }
        }
    }
}

fun main() {
    val eurekaApi = EurekaApiImpl()

    val instanceWrapper = Wrapper(
        Instance(
            instanceId = "192.168.1.10",
            hostName = "192.168.1.10",
            app = "eureka-klient",
            ipAddr = "192.168.1.10",
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

    eurekaApi.register("testapp", instanceWrapper)
}