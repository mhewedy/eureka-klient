# eureka-klient

An netflix `eureka` client written in `kotlin` from the groundup with my bare hands, I used no external libraries except for `JUnit`.
I've written the `JSON` serializer, the deserializer (the most hard part), and the HTTP Client.
(Planning to write a simple HTTP server)

> All the implemenation ment to be perfect or feature-complete, it is just a good enough to enable me for writing the eureka client.

### Infrastrucutre Components:
1. JSON serializer (`helpers.json.Serializer.kt`):
Simple serializer that accepts object and returns json string, with ability to override the generated string for a certain object in the object graph.
2. JSON deserializer/parser (`helpers.json.Deserializer.kt`):
It is not a feature-complete and not confirms to `RFC 7159`, it is just good-enough and do parses most cases you could face in day-to-day (e.g. arrays of objects of arrays of arrays of objects of arrays of scalars)
3. HTTP Client libarary (`helpers.HttpClient.kt`):
A simple wrapper on top of `HttpURLConnection` of Java

### Eureka Client:
Found in `eureka.EurekaApi.kt` file, currently only the `register` function is avaiable, other functions will be added later. 
> It might require to add simple ServerSocket based HTTP server for endpoints like [health check](https://github.com/Netflix/eureka/wiki/Understanding-eureka-client-server-communication)

### How to use:
1. Install eureka server (I've used spring-cloud wrapper from [start.spring.io](https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=2.1.5.RELEASE&baseDir=eurekaserver&groupId=com.example&artifactId=eurekaserver&name=eurekaserver&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.eurekaserver&packaging=jar&javaVersion=1.8&style=cloud-eureka-server)), you can use it to.
 a. after extract the zip file, go to the `EurekaserverApplication` and add annotation: `@org.springframework.cloud.netflix.eureka.server.EnableEurekaServer`  on top of `EurekaserverApplication` and start the server.
2. In `eureka-klient`, go to: `main.kt` file and run the `main` mehtod.
3. Now go to http://localhost:8080 and check your app is registered.
