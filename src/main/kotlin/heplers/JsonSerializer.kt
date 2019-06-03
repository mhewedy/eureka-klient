package heplers

import kotlin.reflect.KCallable
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.extensionReceiverParameter

private val stringType by lazy {
    arrayOf(
        String::class.createType(), String::class.createType(nullable = true)
    )
}

private val numberTypes by lazy {
    arrayOf(
        Byte::class.createType(), Byte::class.createType(nullable = true),
        Short::class.createType(), Short::class.createType(nullable = true),
        Int::class.createType(), Int::class.createType(nullable = true),
        Long::class.createType(), Long::class.createType(nullable = true),
        Float::class.createType(), Float::class.createType(nullable = true),
        Byte::class.createType(), Byte::class.createType(nullable = true)
    )
}

fun serialize(obj: Any?) = obj?.let {

    val properties = obj::class.declaredMembers
        .filter { isProperty(it) }

    if (properties.isNotEmpty()) {
        return@let properties
            .map { serialize(obj, it) }
            .joinToString(prefix = "{", separator = ",", postfix = "}")
    }

    if (obj is Enum<*>) { // enum with no properties
        return@let obj.name.doubleQuote()
    }

    return@let null
}

fun serialize(obj: Any?, property: KCallable<*>): String {
    val value = when {
        property.returnType in numberTypes -> property.call(obj)
        property.returnType in stringType -> (property.call(obj) as String?)?.doubleQuote()
        property.returnType.arguments.isNotEmpty()
                && isCollection(property.returnType) -> serializeCollection(property, obj)
        else -> serialize(property.call(obj))
    }
    return property.name.doubleQuote() + ":" + value
}

fun serializeCollection(property: KCallable<*>, obj: Any?) =
    (property.call(obj) as Collection<*>)
        .map { serialize(it) }
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