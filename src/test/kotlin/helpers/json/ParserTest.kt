package helpers.json

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


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

    @Test
    fun `test parse complex json object from json_dot_org_slash_example_dot_html`() {

        val json = """
            {
              "web-app": {
                "servlet": [
                  {
                    "servlet-name": "cofaxCDS",
                    "servlet-class": "org.cofax.cds.CDSServlet",
                    "init-param": {
                      "configGlossary:installationAt": "Philadelphia, PA",
                      "configGlossary:adminEmail": "ksm@pobox.com",
                      "configGlossary:poweredBy": "Cofax",
                      "configGlossary:poweredByIcon": "/images/cofax.gif",
                      "configGlossary:staticPath": "/content/static",
                      "templateProcessorClass": "org.cofax.WysiwygTemplate",
                      "templateLoaderClass": "org.cofax.FilesTemplateLoader",
                      "templatePath": "templates",
                      "templateOverridePath": "",
                      "defaultListTemplate": "listTemplate.htm",
                      "defaultFileTemplate": "articleTemplate.htm",
                      "useJSP": "false",
                      "jspListTemplate": "listTemplate.jsp",
                      "jspFileTemplate": "articleTemplate.jsp",
                      "cachePackageTagsTrack": "200",
                      "cachePackageTagsStore": "200",
                      "cachePackageTagsRefresh": "60",
                      "cacheTemplatesTrack": "100",
                      "cacheTemplatesStore": "50",
                      "cacheTemplatesRefresh": "15",
                      "cachePagesTrack": "200",
                      "cachePagesStore": "100",
                      "cachePagesRefresh": "10",
                      "cachePagesDirtyRead": "10",
                      "searchEngineListTemplate": "forSearchEnginesList.htm",
                      "searchEngineFileTemplate": "forSearchEngines.htm",
                      "searchEngineRobotsDb": "WEB-INF/robots.db",
                      "useDataStore": "true",
                      "dataStoreClass": "org.cofax.SqlDataStore",
                      "redirectionClass": "org.cofax.SqlRedirection",
                      "dataStoreName": "cofax",
                      "dataStoreDriver": "com.microsoft.jdbc.sqlserver.SQLServerDriver",
                      "dataStoreUrl": "jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon",
                      "dataStoreUser": "sa",
                      "dataStorePassword": "dataStoreTestQuery",
                      "dataStoreTestQuery": "SET NOCOUNT ON;select test='test';",
                      "dataStoreLogFile": "/usr/local/tomcat/logs/datastore.log",
                      "dataStoreInitConns": "10",
                      "dataStoreMaxConns": "100",
                      "dataStoreConnUsageLimit": "100",
                      "dataStoreLogLevel": "debug",
                      "maxUrlLength": "500"
                    }
                  },
                  {
                    "servlet-name": "cofaxEmail",
                    "servlet-class": "org.cofax.cds.EmailServlet",
                    "init-param": {
                      "mailHost": "mail1",
                      "mailHostOverride": "mail2"
                    }
                  },
                  {
                    "servlet-name": "cofaxAdmin",
                    "servlet-class": "org.cofax.cds.AdminServlet"
                  },
                  {
                    "servlet-name": "fileServlet",
                    "servlet-class": "org.cofax.cds.FileServlet"
                  },
                  {
                    "servlet-name": "cofaxTools",
                    "servlet-class": "org.cofax.cms.CofaxToolsServlet",
                    "init-param": {
                      "templatePath": "toolstemplates/",
                      "log": "1",
                      "logLocation": "/usr/local/tomcat/logs/CofaxTools.log",
                      "logMaxSize": "",
                      "dataLog": "1",
                      "dataLogLocation": "/usr/local/tomcat/logs/dataLog.log",
                      "dataLogMaxSize": "",
                      "removePageCache": "/content/admin/remove?cache=pages&id=",
                      "removeTemplateCache": "/content/admin/remove?cache=templates&id=",
                      "fileTransferFolder": "/usr/local/tomcat/webapps/content/fileTransferFolder",
                      "lookInContext": "1",
                      "adminGroupID": "4",
                      "betaServer": "true"
                    }
                  }
                ],
                "servlet-mapping": {
                  "cofaxCDS": "/",
                  "cofaxEmail": "/cofaxutil/aemail/*",
                  "cofaxAdmin": "/admin/*",
                  "fileServlet": "/static/*",
                  "cofaxTools": "/tools/*"
                },
                "taglib": {
                  "taglib-uri": "cofax.tld",
                  "taglib-location": "/WEB-INF/tlds/cofax.tld"
                }
              }
            }
        """

        val actualNode = Parser(json).use {
            it.parse()
        } as ObjectNode

        assertEquals(1, actualNode.props.size)
        assertEquals("web-app", actualNode.props[0].first)
        val webAppElement = actualNode.props[0].second as ObjectNode
        assertEquals(3, webAppElement.props.size)
        assertTrue(webAppElement.props[0].second is ArrayNode)
        val arrayNode: ArrayNode = webAppElement.props[0].second as ArrayNode
        val objNothing = arrayNode.elements[0] as ObjectNode
        assertEquals(objNothing.props[1].second, "org.cofax.cds.CDSServlet")
    }
}