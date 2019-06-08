import eureka.*
import java.net.InetSocketAddress
import java.net.Socket

/**
 * to use the client, you need a eureka server, I suggest use spring-cloud wrapped server.
 * see READEME.md for more details.
 *
 */
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
    println("registered successfully")
}

private fun getMyIPAddr(): String {
    val socket = Socket()
    socket.connect(InetSocketAddress("google.com", 80))
    return socket.localAddress.hostAddress
}
