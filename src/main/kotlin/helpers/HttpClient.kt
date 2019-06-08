package helpers

import helpers.json.Parser
import helpers.json.toJson
import java.io.IOException
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL

enum class Method { GET, POST, PUT, DELETE }

data class Response<R>(val responseCode: Int, val responseText: R)

fun get(
    url: String, headers: Array<Pair<String, String>> = emptyArray(),
    response: Response<String>.() -> Unit
) =
    execute(url, Method.GET, request = null, headers = headers, response = response)

fun <T> post(
    url: String, request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: Response<String>.() -> Unit
) =
    execute(url, Method.POST, request, headers = headers, response = response)

fun <T> put(
    url: String,
    request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: Response<String>.() -> Unit
) =
    execute(url, Method.PUT, request, headers = headers, response = response)

fun <T> delete(
    url: String,
    request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: Response<String>.() -> Unit
) =
    execute(url, Method.DELETE, request, headers = headers, response = response)

fun <T> execute(
    url: String, method: Method, request: T? = null,
    headers: Array<Pair<String, String>> = emptyArray(),
    response: Response<String>.() -> Unit
) {

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
        response.invoke(Response(responseCode, reader.use { it.readText() }))
    }
}

// -- TESTING ....

fun main() {
    data class Post(val userId: Int, val id: Int, val title: String, val body: String)

    val post = Post(userId = 123, id = 456, title = "post title", body = "post body")
    post("https://jsonplaceholder.typicode.com/posts", post) {
        println(responseCode)
        Parser(responseText).use {
            println(it.parse())
        }
    }

    get("https://jsonplaceholder.typicode.com/posts") {
        println(responseCode)
        Parser(responseText).use {
            println(it.parse())
        }
    }
}


