package eureka

import helpers.getprop
import helpers.post
import java.util.*

private val apiBaseUrl = "http://localhost:${getprop("eureka.server.port")}/eureka"

interface EurekaApi {
    fun register(appName: String, instanceInfo: InstanceInfo)
}

class EurekaApiImpl : EurekaApi {

    override fun register(appName: String, instanceInfo: InstanceInfo) {

        println("registering: $appName on port: ${instanceInfo.instance.port.`$`}")

        post("$apiBaseUrl/apps/$appName", instanceInfo, headers = buildHeaders()) {
            if (httpCode != 204) throw Exception(rawResponse)
        }
    }

    private fun buildHeaders(): Array<Pair<String, String>> {
        val headers = arrayListOf<Pair<String, String>>()

        // Authorization header
        val username = getprop("eureka.server.username")
        val password = getprop("eureka.server.password")
        if (username != null && password != null) {
            headers += "Authorization" to "Basic ${Base64.getEncoder()
                .encodeToString("$username:$password".toByteArray())}"
        }

        return headers.toTypedArray()
    }
}