# eureka-klient

A netflix `eureka` client written in `kotlin` from the groundup with bare hands, I've used no external libraries except `JUnit`.
I've written the `JSON` serializer, the deserializer (the most hard part), and the HTTP Client.

> All the implementation meant not to be perfect or feature-complete, it is just a good enough to enable me for writing the eureka client.

### Infrastructure Components:
1. **JSON serializer** (`helpers.json.Serializer.kt`):

    Simple serializer that accepts object and returns json string, with ability to override the generated string for a certain object in the object graph.

2. **JSON deserializer/parser** (`helpers.json.Deserializer.kt`):

    It is not a feature-complete and not confirms to `RFC 7159`, it is just good-enough and do parses most cases you could face in day-to-day (e.g. arrays of objects of arrays of arrays of objects of arrays of scalars)

3. **HTTP Client library** (`helpers.HttpClient.kt`):

    A simple wrapper on top of `HttpURLConnection` of Java

### Eureka Client:
Found in `eureka.EurekaApi.kt` file, the `register` and `renew` functions are implemented.

### How to use:
1. Install eureka server by either:

 a. spring-cloud from [start.spring.io](https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=2.1.5.RELEASE&baseDir=eurekaserver&groupId=com.example&artifactId=eurekaserver&name=eurekaserver&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.eurekaserver&packaging=jar&javaVersion=1.8&style=cloud-eureka-server), you can use it to.

  * After extract the zip file, go to the `EurekaserverApplication` and add annotation: `@org.springframework.cloud.netflix.eureka.server.EnableEurekaServer`  on top of `EurekaserverApplication` :
  ```java
 @org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
 @SpringBootApplication
 public class EurekaserverApplication {

     public static void main(String[] args) {
         SpringApplication.run(EurekaserverApplication.class, args);
     }

 }
 ```
  * Now run:
  `mvn spring-boot:run`
  
  b. You can also use jhispter register instead of building your own spring-boot based registery [docker image](https://hub.docker.com/r/jhipster/jhipster-registry)
 
2. In `eureka-klient`, go to: `main.kt` file and run the `main` function to start the client.
  You can register multiple clients by changing the client name and the port via cli:
```bash  
   mvn clean package -DskipTests
   EUREKA_SERVER_PORT=8080 java -jar target/eureka-klient-*-jar-with-dependencies.jar svc1 8081
```
3. Now go to http://localhost:8080 and check your app is registered. You can verify apps are registered using:

   `curl -H 'Accept: application/json'  http://localhost:8080/eureka/apps | jq`

    you will got output similar to:

```js
{
  "applications": {
    "versions__delta": "1",
    "apps__hashcode": "UP_2_",
    "application": [
      {
        "name": "MYSERVICE",
        "instance": [
          {
            "instanceId": "192.168.1.10",
            "hostName": "192.168.1.10",
            "app": "MYSERVICE",
            "ipAddr": "192.168.1.10",
            "status": "UP",
            "overriddenStatus": "UNKNOWN",
            "port": {
              "$": 8089,
              "@enabled": "true"
            },
            // ....... rest of json string ....... 
```
4. You can also call the client running on step 2 above from spring-cloud eureka client:

```java

@EnableDiscoveryClient
@SpringBootApplication
class DemoApplication : CommandLineRunner {

    @Autowired
    lateinit var restTemplate: RestTemplate
    @Autowired
    lateinit var discoveryClient: DiscoveryClient

    override fun run(vararg args: String?) {

        println(discoveryClient.getInstances("svc1"))

        val entity = restTemplate.getForEntity("http://svc1", String::class.java)
        println(entity.statusCode)
        println(entity.body)

    }

    @Bean
    @LoadBalanced
    fun restTemplate(): RestTemplate = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

```
### Why?
I am working on app with bloated unnecessary services that most of the time is just pain-in-the-head to got all of the them running and registered to the eureka registery to do absloutly nothing important. So I decided to write a simple client that can work as a drop-in replacemnt.
