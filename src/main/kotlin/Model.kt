class Instance(
    var hostName: String,
    var app: String,
    var ipAddr: String,
    var vipAddress: String,
    var secureVipAddress: String,
    var status: StatusType,
    var port: Long,
    var securePort: Long,
    var homePageUrl: String,
    var statusPageUrl: String,
    var healthCheckUrl: String,
    var dataCenterInfo: DataCenterInfo,
    var leaseInfo: LeaseInfo,
    var metadata: AppMetadataType
)

class DataCenterInfo(var name: DcNameType, var metadata: AmazonMetdataType)

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

class LeaseInfo(var evictionDurationInSecs: Long)

class AppMetadataType(var any: Map<String, Any?>?)

enum class DcNameType(private val value: String) {
    MY_OWN("MyOwn"),
    AMAZON("Amazon");
}

enum class StatusType {
    UP,
    DOWN,
    STARTING,
    OUT_OF_SERVICE,
    UNKNOWN;
}
