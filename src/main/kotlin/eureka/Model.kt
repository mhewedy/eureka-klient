package eureka

data class Wrapper(val instance: Instance)

class Instance(
    var hostName: String,
    var app: String,
    var ipAddr: String,
    var vipAddress: String,
    var secureVipAddress: String,
    var status: StatusType,
    var port: Long? = null,
    var securePort: Long,
    var homePageUrl: String,
    var statusPageUrl: String,
    var healthCheckUrl: String,
    var dataCenterInfo: DataCenterInfo,
    var leaseInfo: LeaseInfo? = null,
    var metadata: AppMetadataType? = null
)

class DataCenterInfo(
    var name: DcNameType,
    val `@class`: String,
    var metadata: AmazonMetdataType? = null
)

class LeaseInfo(var evictionDurationInSecs: Long? = null)

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
    var amiLaunchIndex: String,
    var localHostname: String,
    var availabilityZone: String,
    var instanceId: String,
    var publicIpv4: String,
    var publicHostname: String,
    var amiManifestPath: String,
    var localIpv4: String,
    var hostname: String,
    var amiId: String,
    var instanceType: String
)

class AppMetadataType(var any: Map<String, Any?>?)
