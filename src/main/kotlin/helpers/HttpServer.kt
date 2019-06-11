package helpers

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun startServer(port: Int) {
    val server =
        HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/") {
        it.responseHeaders.apply { add(CONTENT_TYPE, APPLICATION_JSON) }
        it.sendResponseHeaders(200, 0)
        it.responseBody.use { stream ->
            stream.write(ByteArray(0))
        }
    }
    server.executor = null
    server.start()
    println("started http server on port: $port")
}