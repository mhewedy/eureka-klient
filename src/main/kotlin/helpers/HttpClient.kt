package helpers

import helpers.json.Node
import helpers.json.Parser
import helpers.json.toJson
import java.io.IOException
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL

enum class Method { GET, POST, PUT, DELETE }

data class Response<R>(val httpCode: Int, val rawResponse: String, val response: R)

fun get(
    url: String, headers: Array<Pair<String, String>> = emptyArray(),
    response: (Response<Node>.() -> Unit)? = null
) = execute(url, Method.GET, request = null, headers = headers, response = response)

fun <T> post(
    url: String, request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: (Response<Node>.() -> Unit)? = null
) = execute(url, Method.POST, request, headers = headers, response = response)

fun <T> put(
    url: String,
    request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: (Response<Node>.() -> Unit)? = null
) = execute(url, Method.PUT, request, headers = headers, response = response)

fun <T> delete(
    url: String,
    request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: (Response<Node>.() -> Unit)? = null
) = execute(url, Method.DELETE, request, headers = headers, response = response)

fun <T> execute(
    url: String, method: Method, request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: (Response<Node>.() -> Unit)? = null
): Node {

    with(URL(url).openConnection() as HttpURLConnection) {
        requestMethod = method.name

        headers.forEach { addRequestProperty(it.first, it.second) }

        if (method in arrayOf(Method.POST, Method.PUT, Method.DELETE)) {
            request?.let {
                addRequestProperty("Content-Type", "application/json")
                addRequestProperty("Accept", "application/json")
                doOutput = true
                outputStream.write(request.toJson(ignoreNull = true)?.toByteArray())
                outputStream.close()
            }
        }

        val reader = try {
            inputStream.bufferedReader()
        } catch (ex: IOException) {
            StringReader(ex.message)
        }
        val rawResponse = reader.use { it.readText() }
        val responseNode = Response(responseCode, rawResponse, Parser(rawResponse).use { it.parse() })

        response?.invoke(responseNode)

        return responseNode.response
    }
}

// -- TESTING ....

fun main() {
    val node = get("https://jsonplaceholder.typicode.com/posts") {
        println(httpCode)
    }
    println(node)
}


