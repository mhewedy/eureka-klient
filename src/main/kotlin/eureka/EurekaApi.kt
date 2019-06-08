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
            dataCenterInfo = DataCenterInfo(
                DcNameType.MY_OWN,
                "com.netflix.appinfo.MyDataCenterInfo"
            )
        )
    )

    eurekaApi.register("testapp", instanceWrapper)
}