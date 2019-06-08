package eureka

data class Wrapper(val instance: Instance)

class Instance(
    val instanceId: String,
    val hostName: String,
    val app: String,
    val ipAddr: String,
    val vipAddress: String? = null,
    val secureVipAddress: String? = null,
    val status: StatusType,
    val overriddenStatus: StatusType,
    val port: Port,
    val securePort: Port,
    val homePageUrl: String? = null,
    val statusPageUrl: String? = null,
    val healthCheckUrl: String? = null,
    val countryId: Int,
    val dataCenterInfo: DataCenterInfo,
    val leaseInfo: LeaseInfo? = null,
    val metadata: AppMetadataType? = null
)

class Port(val `$`: Int, val `@enabled`: String)

class DataCenterInfo(
    val name: DcNameType,
    val `@class`: String,
    val metadata: AmazonMetdataType? = null
)

class LeaseInfo(val evictionDurationInSecs: Long? = null)

enum class DcNameType(val value: String) {
    MY_OWN("MyOwn"),
    AMAZON("Amazon");

    fun toJson() = """"$value""""
}

enum class StatusType {
    UP,
    DOWN,
    STARTING,
    OUT_OF_SERVICE,
    UNKNOWN;
}

class AmazonMetdataType(
    val amiLaunchIndex: String,
    val localHostname: String,
    val availabilityZone: String,
    val instanceId: String,
    val publicIpv4: String,
    val publicHostname: String,
    val amiManifestPath: String,
    val localIpv4: String,
    val hostname: String,
    val amiId: String,
    val instanceType: String
)

class AppMetadataType(val any: Map<String, Any?>?)
