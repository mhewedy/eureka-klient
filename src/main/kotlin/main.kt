import eureka.*
import helpers.getopt
import helpers.startServer
import java.lang.Thread.sleep
import java.net.InetAddress

/**
 * to use the client, you need a eureka server, I suggest use spring-cloud wrapped server.
 * see READEME.md for more details.
 *
 */
fun main(args: Array<String>) {

    val eurekaApi = EurekaApi.create()

    val myIP = InetAddress.getLocalHost().hostAddress
    val app = getopt(args, 0, "eureka-klient")
    val port = getopt(args, 1, 8080).toInt()

    startServer(port)

    eurekaApi.register(
        app, InstanceInfo(
            Instance(
                app = app,
                ipAddr = myIP,
                hostName = myIP,
                instanceId = myIP,
                vipAddress = app,
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
    println("registered: $app on port: $port")

    while (true) {
        sleep(60 * 1000)
        eurekaApi.renew(app, myIP)
    }
}