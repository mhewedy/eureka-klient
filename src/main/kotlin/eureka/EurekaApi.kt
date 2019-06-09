package eureka

import helpers.getprop
import helpers.post
import helpers.put
import java.util.*

private val apiBaseUrl = "http://localhost:${getprop("eureka.server.port")}/eureka"

interface EurekaApi {
    companion object {
        fun create(): EurekaApi = EurekaApiImpl()
    }

    fun register(app: String, instanceInfo: InstanceInfo)

    fun renew(app: String, instanceId: String)
}

private class EurekaApiImpl : EurekaApi {

    override fun register(app: String, instanceInfo: InstanceInfo) {
        post("$apiBaseUrl/apps/$app", instanceInfo, headers = buildHeaders()) {
            if (204 != httpCode) throw Exception("$httpCode: $rawResponse")
        }
    }

    override fun renew(app: String, instanceId: String) {
        put<Unit>("$apiBaseUrl/apps/$app/$instanceId", headers = buildHeaders()) {
            if (200 != httpCode) throw Exception("$httpCode: $rawResponse")
        }
    }

    // -- private

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