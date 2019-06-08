package eureka

import helpers.post

const val apiBaseUrl = "http://localhost:8761/eureka"

interface EurekaApi {
    fun register(appName: String, instanceInfo: InstanceInfo)
}

class EurekaApiImpl : EurekaApi {

    override fun register(appName: String, instanceInfo: InstanceInfo) {
        post("$apiBaseUrl/apps/$appName", instanceInfo) {
            if (httpCode != 204) throw Exception(rawResponse)
        }
    }
}