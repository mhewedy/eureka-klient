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
Found in `eureka.EurekaApi.kt` file, currently only the `register` function is avaiable, other functions will be added later. 
> It might require to add simple ServerSocket based HTTP server for endpoints like [health check](https://github.com/Netflix/eureka/wiki/Understanding-eureka-client-server-communication)

### How to use:
1. Install eureka server (I've used spring-cloud wrapper from [start.spring.io](https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=2.1.5.RELEASE&baseDir=eurekaserver&groupId=com.example&artifactId=eurekaserver&name=eurekaserver&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.eurekaserver&packaging=jar&javaVersion=1.8&style=cloud-eureka-server)), you can use it to.

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
 
2. In `eureka-klient`, go to: `main.kt` file and run the `main` function to start the client.
3. Now go to http://localhost:8080 and check your app is registered.

### Why?
I am working on app with bloated unnecessary services that most of the time is just pain-in-the-head to got all of the them running and registered to the eureka registery to do absloutly nothing. So I decided to write a simple client that can work as a drop-in replacemnt.
