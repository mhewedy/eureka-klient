package eureka

import helpers.post
import java.net.InetSocketAddress
import java.net.Socket

const val apiBaseUrl = "http://localhost:8761/eureka"

interface EurekaApi {
    fun register(appName: String, instanceInfo: InstanceInfo)
}

class EurekaApiImpl : EurekaApi {

    override fun register(appName: String, instanceInfo: InstanceInfo) {
        post("$apiBaseUrl/apps/$appName", instanceInfo) {
            if (responseCode != 204) throw Exception(responseText)
        }
    }
}

fun main() {
    val eurekaApi = EurekaApiImpl()

    val myIP = getMyIPAddr()
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

private fun getMyIPAddr(): String {
    val socket = Socket()
    socket.connect(InetSocketAddress("google.com", 80))
    return socket.localAddress.hostAddress
}
