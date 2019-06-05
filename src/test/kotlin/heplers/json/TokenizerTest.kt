package heplers.json

import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {

    @Test
    fun `test readUntil`() {
        val tokenizer = Tokenizer("this is the value \"stop use it")
        val beforeDelimiter = tokenizer.readUntil('"')
        val rest = tokenizer.reader.readText()
        tokenizer.close()

        assertEquals("this is the value ", beforeDelimiter)
        assertEquals("stop use it", rest)
    }

    @Test
    fun `test readUntil not found`() {
        val tokenizer = Tokenizer("this is the value \"stop use it")
        val beforeDelimiter = tokenizer.readUntil('X')
        tokenizer.close()
        assertEquals("this is the value \"stop use it", beforeDelimiter)
    }

    @Test
    fun `test skip when found`() {
        val tokenizer = Tokenizer("   hello")
        tokenizer.skipWhiteSpace()
        assertEquals("hello", tokenizer.reader.readText())
        tokenizer.close()
    }

    @Test
    fun `test skip white space when found`() {
        val tokenizer = Tokenizer("\nhello")
        tokenizer.skipWhiteSpace()
        assertEquals("hello", tokenizer.reader.readText())
        tokenizer.close()
    }

    @Test
    fun `test skip when not found`() {
        Tokenizer("hello").use {
            it.skipWhiteSpace()
            assertEquals("hello", it.reader.readText())
        }
    }

    @Test
    fun `test parse can parse nested json object parsing`() {

        val json = """
            {"name":{"firstName":"wael"},"age":"30"}
        """.trimIndent()

        val actualNode = Tokenizer(json).use {
            it.parse()
        }

        assertEquals(
            ObjectNode(
                arrayListOf(
                    "name" to ObjectNode(arrayListOf("firstName" to "wael")),
                    "age" to "30"
                )
            ),
            actualNode
        )
    }

    @Test
    fun `test parse can parse list of nested json object parsing`() {

        val json = """
            [{"name":"wael"},{"name":{"firstName":"wael"},"age":"30"}]
        """.trimIndent()

        val actualNode = Tokenizer(json).use {
            it.parse()
        }

        assertEquals(
            ArrayNode(
                arrayListOf(
                    ObjectNode(arrayListOf("name" to "wael")),
                    ObjectNode(
                        arrayListOf(
                            "name" to ObjectNode(arrayListOf("firstName" to "wael")),
                            "age" to "30"
                        )
                    )
                )
            ),
            actualNode
        )
    }

    @Test
    fun `test parse array of array of array of objects`() {

        val json = """
            [[[{"name":"abc","age":"30"},{"name":"efg"}]]]
        """.trimIndent()

        val actualNode = Tokenizer(json).use {
            it.parse()
        }

        assertEquals(
            ArrayNode(
                arrayListOf(
                    ArrayNode(
                        arrayListOf(
                            ArrayNode(
                                arrayListOf(
                                    ObjectNode(arrayListOf("name" to "abc", "age" to "30")),
                                    ObjectNode(arrayListOf("name" to "efg"))
                                )
                            )
                        )
                    )
                )
            ),
            actualNode
        )
    }
}