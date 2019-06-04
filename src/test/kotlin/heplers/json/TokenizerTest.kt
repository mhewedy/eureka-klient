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
}