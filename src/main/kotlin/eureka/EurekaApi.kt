package eureka

import helpers.post

const val apiBaseUrl = "http://localhost:8761/eureka"

interface EurekaApi {
    fun register(app: String, instance: Instance)
}

class EurekaApiImpl : EurekaApi {

    override fun register(app: String, instance: Instance) {

        post("$apiBaseUrl/apps/$app", instance) {
            println(responseCode)
            println(responseText)
        }

    }

}

fun main() {
    val eurekaApi = EurekaApiImpl()

    val instance = Instance(
        hostName = "localhost",
        app = "eureka-client-test",
        ipAddr = "192.168.1.10",
        secureVipAddress = "192.168.1.11",
        status = StatusType.UP,
        vipAddress = "192.168.1.12",
        securePort = 0,
        homePageUrl = "http://localhost/home-page-url",
        statusPageUrl = "http://localhost/status-page-url",
        healthCheckUrl = "http://localhost/health-check-url",
        dataCenterInfo = DataCenterInfo(DcNameType.MY_OWN)
    )

    eurekaApi.register("testapp", instance)
}