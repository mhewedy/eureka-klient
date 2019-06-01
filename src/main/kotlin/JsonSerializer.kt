import kotlin.reflect.KCallable
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.extensionReceiverParameter

val stringType = String::class.createType()
val numberTypes = arrayOf(
    Byte::class.createType(),
    Short::class.createType(),
    Int::class.createType(),
    Long::class.createType(),
    Float::class.createType(),
    Double::class.createType()
)

fun serialize(obj: Any, property: KCallable<*>): String {

    val returnType = property.returnType

    val value = when {
        returnType in numberTypes -> "${property.call(obj)}"
        returnType == stringType -> """"${property.call(obj)}""""
        returnType.arguments.isNotEmpty() && isCollection(returnType) -> {
            serializeCollection(property, obj)
        }
        else -> serialize(property.call(obj) as Any)
    }

    return """"${property.name}":$value"""
}

fun serialize(obj: Any): String {
    return obj::class.declaredMembers
        .filter { isProperty(it) }
        .map { serialize(obj, it) }
        .joinToString(prefix = "{", separator = ",", postfix = "}")
}

fun serializeCollection(property: KCallable<*>, obj: Any): String {
    return (property.call(obj) as Collection<*>)
        .map { serialize(it as Any) }
        .joinToString(prefix = "[", separator = ",", postfix = "]")
}

private fun isProperty(it: KCallable<*>) =
    it.extensionReceiverParameter == null && it is KProperty1<*, *>

private fun isCollection(returnType: KType) =
    returnType == Collection::class.createType(returnType.arguments) ||
            returnType == Set::class.createType(returnType.arguments) ||
            returnType == List::class.createType(returnType.arguments)

fun Any.toJson() = serialize(this)