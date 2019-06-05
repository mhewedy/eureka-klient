package helpers.json

import kotlin.test.Test
import kotlin.test.assertEquals


class ParserTest {

    @Test
    fun `test readUntil`() {
        val parser = Parser("this is the value \"stop use it")
        val beforeDelimiter = parser.readUntil('"')
        val rest = parser.reader.readText()
        parser.close()

        assertEquals("this is the value ", beforeDelimiter)
        assertEquals("stop use it", rest)
    }

    @Test
    fun `test readUntil not found`() {
        val parser = Parser("this is the value \"stop use it")
        val beforeDelimiter = parser.readUntil('X')
        parser.close()
        assertEquals("this is the value \"stop use it", beforeDelimiter)
    }

    @Test
    fun `test skip when found`() {
        val parser = Parser("   hello")
        parser.skipWhiteSpaces()
        assertEquals("hello", parser.reader.readText())
        parser.close()
    }

    @Test
    fun `test skip white space when found`() {
        val parser = Parser("\nhello")
        parser.skipWhiteSpaces()
        assertEquals("hello", parser.reader.readText())
        parser.close()
    }

    @Test
    fun `test skip when not found`() {
        Parser("hello").use {
            it.skipWhiteSpaces()
            assertEquals("hello", it.reader.readText())
        }
    }

    @Test
    fun `test parse can parse nested json object parsing`() {

        val json = """
            {
              "name": {
                "firstName": "wael"
              },
              "age": "30"
            }
        """
        val actualNode = Parser(json).use {
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
            [
              {
                "name": "wael"
              },
              {
                "name": {
                  "firstName": "wael"
                },
                "age": "30"
              }
            ]
        """

        val actualNode = Parser(json).use {
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
            [
              [
                [
                  {
                    "name": "abc",
                    "age": "30"
                  },
                  {
                    "name": "efg"
                  }
                ]
              ]
            ]
        """

        val actualNode = Parser(json).use {
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

    @Test
    fun `test parse object of array of objects`() {

        val json = """
            {
              "occupations": [
                {
                  "title": "Clark",
                  "grade": "10",
                  "salary": "300"
                },
                {
                  "title": "Accountant",
                  "grade": "n/a",
                  "salary": "150"
                }
              ]
            }
        """

        val actualNode = Parser(json).use {
            it.parse()
        }

        assertEquals(
            ObjectNode(
                arrayListOf(
                    "occupations" to ArrayNode(
                        arrayListOf(
                            ObjectNode(arrayListOf("title" to "Clark", "grade" to "10", "salary" to "300")),
                            ObjectNode(arrayListOf("title" to "Accountant", "grade" to "n/a", "salary" to "150"))
                        )
                    )
                )
            ),
            actualNode
        )
    }

    @Test
    fun `object of array of objects of array of objects`() {
        val json = """
            {
              "type": "1",
              "model": [
                {
                  "years": [
                    {
                      "name": "elephant",
                      "digits": "2019"
                    },
                    {
                      "name": "joungle",
                      "digits": "2018"
                    }
                  ]
                },
                {
                  "years": [
                    {
                      "name": "donkey",
                      "digits": "2015"
                    },
                    {
                      "name": "tiger",
                      "digits": "1009"
                    }
                  ]
                }
              ]
            }
        """

        val actualNode = Parser(json).use {
            it.parse()
        }

        assertEquals(
            ObjectNode(
                arrayListOf(
                    "type" to "1",
                    "model" to ArrayNode(
                        arrayListOf(
                            ObjectNode(
                                arrayListOf(
                                    "years" to ArrayNode(
                                        arrayListOf(
                                            ObjectNode(arrayListOf("name" to "elephant", "digits" to "2019")),
                                            ObjectNode(arrayListOf("name" to "joungle", "digits" to "2018"))
                                        )
                                    )
                                )
                            ),
                            ObjectNode(
                                arrayListOf(
                                    "years" to ArrayNode(
                                        arrayListOf(
                                            ObjectNode(arrayListOf("name" to "donkey", "digits" to "2015")),
                                            ObjectNode(arrayListOf("name" to "tiger", "digits" to "1009"))
                                        )
                                    )
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