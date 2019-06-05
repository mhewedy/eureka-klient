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
        val tokenizer = Tokenizer("hello")
        tokenizer.skipWhiteSpace()
        assertEquals("hello", tokenizer.reader.readText())
        tokenizer.close()
    }

    @Test
    fun `test parse can parse nested json object parsing`() {

        val json = """
            {"name":{"firstName":"wael"},"age":"30"}
        """.trimIndent()

        val tokenizer = Tokenizer(json)
        val actualNode = tokenizer.parse()
        tokenizer.close()

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
}