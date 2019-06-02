import heplers.toJson
import kotlin.test.Test

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
            dataCenterInfo = DataCenterInfo(DcNameType.MY_OWN)
        )

        println(instance.toJson())
    }
}