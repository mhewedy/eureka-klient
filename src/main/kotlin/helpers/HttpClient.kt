package helpers

import helpers.json.Parser
import helpers.json.toJson
import java.net.HttpURLConnection
import java.net.URL

enum class Method { GET, POST, PUT, DELETE }

data class Response<R>(val responseCode: Int, val responseText: R)

fun get(url: String, response: Response<String>.() -> Unit) =
    execute(url, Method.GET, request = null, response = response)

fun <T> post(url: String, request: T? = null, response: Response<String>.() -> Unit) =
    execute(url, Method.POST, request, response = response)

fun <T> put(url: String, request: T? = null, response: Response<String>.() -> Unit) =
    execute(url, Method.PUT, request, response = response)

fun <T> delete(url: String, request: T? = null, response: Response<String>.() -> Unit) =
    execute(url, Method.DELETE, request, response = response)

fun <T> execute(url: String, method: Method, request: T? = null, response: Response<String>.() -> Unit) {

    with(URL(url).openConnection() as HttpURLConnection) {
        requestMethod = method.name

        if (method in arrayOf(Method.POST, Method.PUT, Method.DELETE)) {
            request?.let {
                doOutput = true
                outputStream.write(request.toJson()?.toByteArray())
                outputStream.close()
            }
        }
        val responseText = inputStream.bufferedReader().readText()
        inputStream.close()

        response.invoke(Response(responseCode, responseText))
    }
}

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


