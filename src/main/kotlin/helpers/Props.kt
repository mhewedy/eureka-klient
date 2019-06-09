package helpers

import java.io.InputStreamReader
import java.util.*

// opts
fun getopt(args: Array<String>, number: Int, default: Any) =
    if (args.size >= number + 1) args[number] else default.toString()
// ~ opts

// props
private val properties = InputStreamReader(
    {}::class.java.classLoader.getResourceAsStream("application.properties")
).use {
    val props = Properties()
    props.load(it)
    props
}

private val funIterator = arrayOf(
    fun(key: String) = System.getenv(key.toUpperCase().replace('.', '_').replace('-', '_')),
    fun(key: String) = System.getProperty(key),
    fun(key: String) = properties[key]?.toString()
).iterator()

fun getProperty(key: String, default: String) = getProperty(key) ?: default
fun getProperty(key: String, default: Int) = getProperty(key)?.toInt() ?: default

fun getProperty(key: String): String? {
    var value: String? = null
    while (value == null && funIterator.hasNext()) {
        value = funIterator.next().invoke(key)
    }
    return value
}
// ~ props