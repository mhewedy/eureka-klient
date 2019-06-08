package eureka

import helpers.post

const val apiBaseUrl = "http://localhost:8080/eureka"

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