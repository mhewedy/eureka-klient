import eureka.*
import helpers.getopt
import java.lang.Thread.sleep
import java.net.InetSocketAddress
import java.net.Socket

/**
 * to use the client, you need a eureka server, I suggest use spring-cloud wrapped server.
 * see READEME.md for more details.
 *
 */
fun main(args: Array<String>) {

    val eurekaApi = EurekaApiImpl()

    val myIP = getMyIPAddr()
    val app = getopt(args, 0, "eureka-klient")
    val port = getopt(args, 1, 8080).toInt()

    println("registering: $app on port: $port")

    eurekaApi.register(
        app, InstanceInfo(
            Instance(
                app = app,
                ipAddr = myIP,
                hostName = myIP,
                instanceId = myIP,
                status = StatusType.UP,
                overriddenStatus = StatusType.UNKNOWN,
                port = Port(port, "true"),
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

    while (true) {
        sleep(60 * 1000)
        println("renew lease for: $app/$myIP")
        eurekaApi.renew(app, myIP)
    }
}

private fun getMyIPAddr(): String {
    val socket = Socket()
    socket.connect(InetSocketAddress("google.com", 80))
    return socket.localAddress.hostAddress
}
