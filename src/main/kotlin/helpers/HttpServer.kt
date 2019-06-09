package helpers

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun startServer(port: Int) {
    val server =
        HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/") {
        val response = "OK".toByteArray()
        it.sendResponseHeaders(200, response.size.toLong())
        it.responseBody.use { stream ->
            stream.write(response)
        }
    }
    server.executor = null
    server.start()
    println("started http server on port: $port")
}