package eureka

import helpers.getprop
import helpers.post

private val apiBaseUrl = "http://localhost:${getprop("eureka.server.port")}/eureka"

interface EurekaApi {
    fun register(appName: String, instanceInfo: InstanceInfo)
}

class EurekaApiImpl : EurekaApi {

    override fun register(appName: String, instanceInfo: InstanceInfo) {
        println("registering: $appName on port: ${instanceInfo.instance.port.`$`}")
        post("$apiBaseUrl/apps/$appName", instanceInfo) {
            if (httpCode != 204) throw Exception(rawResponse)
        }
    }
}