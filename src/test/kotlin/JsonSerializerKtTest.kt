import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredMembers


class JsonSerializerKtTest {

    @Test
    fun `can serialize numbers`() {

        class IntDummy(val x: Int = 10)
        class FloatDummy(val x: Float = 10.4f)

        val intDummy = IntDummy()
        assertEquals(
            """"x":10""", serialize(
                intDummy,
                intDummy::class.declaredMembers.first()
            )
        )

        val floatDummy = FloatDummy()
        assertEquals(
            """"x":10.4""", serialize(
                floatDummy,
                floatDummy::class.declaredMembers.first()
            )
        )
    }

    @Test
    fun `can serialize strings`() {
        class StringDummy(val s: String = "Yes")

        val stringDummy = StringDummy()
        assertEquals(
            """"s":"Yes"""", serialize(
                stringDummy,
                stringDummy::class.declaredMembers.first()
            )
        )
    }

    @Test
    fun `can serialize object of one primitive`() {
        class StringDummy(val s: String = "Yes")
        assertEquals("""{"s":"Yes"}""", serialize(StringDummy()))
    }

    @Test
    fun `can serialize object of object`() {
        data class User(val name: String, val age: Int)
        data class Course(val user: User)

        val user = User("ali", 30)
        val course = Course(user)
        assertEquals("""{"user":{"age":30,"name":"ali"}}""", serialize(course))
    }

    @Test
    fun listOfObjects() {
        data class User(val name: String, val age: Int)
        data class Course(val users: List<User>)

        val course = Course(arrayListOf(User("ali", 30), User("mostafa", 20), User("monsour", 40)))
        assertEquals(
            """{"users":[{"age":30,"name":"ali"},{"age":20,"name":"mostafa"},{"age":40,"name":"monsour"}]}""",
            serialize(course)
        )
    }

    @Test
    fun addExtensionFunInAnyObject() {
        data class User(val name: String, val age: Int)
        data class Course(val users: List<User>)

        val course = Course(arrayListOf(User("ali", 30), User("mostafa", 20), User("monsour", 40)))
        assertEquals(
            """{"users":[{"age":30,"name":"ali"},{"age":20,"name":"mostafa"},{"age":40,"name":"monsour"}]}""",
            course.toJson()
        )
    }

    @Test
    fun `can serialize object of one nullable primitive`() {
        class StringDummy(val s: String? = "Yes")
        assertEquals("""{"s":"Yes"}""", serialize(StringDummy()))
    }

    @Test
    fun supportNullableTypes() {
        data class User(val name: String?, val age: Int)
        data class Course(val users: List<User>)

        val course = Course(arrayListOf(User(null, 30), User("mostafa", 20), User("monsour", 40)))

        assertEquals(
            """{"users":[{"age":30,"name":null},{"age":20,"name":"mostafa"},{"age":40,"name":"monsour"}]}""",
            course.toJson()
        )
    }
}
