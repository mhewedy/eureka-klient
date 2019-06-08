package eureka

import helpers.json.toJson
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelTest {

    @Test
    fun `test serialization`() {
        val instance = Instance(
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
            dataCenterInfo = DataCenterInfo(DcNameType.MY_OWN, "")
        )

        val expected = """{
              "app": "eureka-client-test",
              "dataCenterInfo": {
                "@class":"",
                "metadata": null,
                "name": {
                  "value": "MyOwn"
                }
              },
              "healthCheckUrl": "http://localhost/health-check-url",
              "homePageUrl": "http://localhost/home-page-url",
              "hostName": "localhost",
              "ipAddr": "192.168.1.10",
              "leaseInfo": null,
              "metadata": null,
              "port": null,
              "securePort": 0,
              "secureVipAddress": "192.168.1.11",
              "status": "UP",
              "statusPageUrl": "http://localhost/status-page-url",
              "vipAddress": "192.168.1.12"
            }""".replace("\\s+".toRegex(), "")

        assertEquals(expected, instance.toJson())
    }
}