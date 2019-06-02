import kotlin.reflect.KCallable
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.extensionReceiverParameter

private val stringType by lazy { String::class.createType() }

private val numberTypes by lazy {
    arrayOf(
        Byte::class.createType(),
        Short::class.createType(),
        Int::class.createType(),
        Long::class.createType(),
        Float::class.createType(),
        Double::class.createType()
    )
}

fun serialize(obj: Any) = obj::class.declaredMembers
    .filter { isProperty(it) }
    .map { serialize(obj, it) }
    .joinToString(prefix = "{", separator = ",", postfix = "}")

fun serialize(obj: Any, property: KCallable<*>): String {
    val value = when {
        property.returnType in numberTypes -> property.call(obj)
        property.returnType == stringType -> (property.call(obj) as String).doubleQuote()
        property.returnType.arguments.isNotEmpty()
                && isCollection(property.returnType) -> serializeCollection(property, obj)
        else -> serialize(property.call(obj) as Any)
    }
    return property.name.doubleQuote() + ":" + value
}

fun serializeCollection(property: KCallable<*>, obj: Any) =
    (property.call(obj) as Collection<*>)
        .map { serialize(it as Any) }
        .joinToString(prefix = "[", separator = ",", postfix = "]")

private fun isProperty(it: KCallable<*>) =
    it.extensionReceiverParameter == null && it is KProperty1<*, *>

private fun isCollection(returnType: KType) =
    returnType in arrayOf(
        Collection::class.createType(returnType.arguments),
        Set::class.createType(returnType.arguments),
        List::class.createType(returnType.arguments)
    )

fun Any.toJson() = serialize(this)

private fun String.doubleQuote() = """"$this""""