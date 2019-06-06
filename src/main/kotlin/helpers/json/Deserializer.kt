package helpers.json

import helpers.json.Token.colon
import helpers.json.Token.comma
import helpers.json.Token.doubleQuote
import helpers.json.Token.leftBrace
import helpers.json.Token.leftBracket
import helpers.json.Token.rightBrace
import helpers.json.Token.rightBracket
import java.io.Closeable
import java.io.PushbackReader
import java.io.StringWriter

// 1: writing tokenizer
// 2: building AST
// 3: convert AST to object graph

private object Token {
    const val leftBracket = '['
    const val rightBracket = ']'
    const val leftBrace = '{'
    const val rightBrace = '}'
    const val doubleQuote = '"'
    const val comma = ','
    const val colon = ':'
}

sealed class Node
object EmptyNode : Node()
data class ValueNode(val value: Any?) : Node()
data class ObjectNode(val props: ArrayList<Pair<String, Any?>>) : Node()
data class ArrayNode(val elements: ArrayList<Node>) : Node()

class Parser(json: String) : Closeable {
    val reader = PushbackReader(json.reader())

    fun parse(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {

        when (currentChar()) {
            leftBrace -> {
                return parseObject(elements = elements)
            }
            rightBrace -> {
                return ObjectNode(props)
            }
            leftBracket -> {
                elements += parse()
                return parse(elements = elements)
            }
            rightBracket -> {
                return ArrayNode(elements)
            }
            comma -> {
                if (elements.isNotEmpty()) {
                    elements += parse(props)
                    return parse(props, elements)
                }
                if (props.isNotEmpty()) {
                    return parseObject(props, elements)
                }
            }
        }
        return EmptyNode
    }

    private fun parseObject(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {
        readUntil(doubleQuote)
        val key = readUntil(doubleQuote)
        readUntil(colon)
        skipWhiteSpaces()
        when (val nextChar = currentChar()) {
            doubleQuote -> {
                val value = readUntil(doubleQuote)
                props += Pair(key, value)
                return parse(props, elements)
            }
            leftBrace -> {
                unread(nextChar)
                props += Pair(key, parse(elements = elements))
                return parse(props, elements)
            }
            leftBracket -> {
                elements += parse(props)
                props += Pair(key, parse(elements = elements))
                return parse(props)
            }
        }
        return EmptyNode
    }

    private fun currentChar(): Char {
        skipWhiteSpaces()
        return reader.read().toChar()
    }

    private fun unread(char: Char) = reader.unread(char.toInt())

    fun readUntil(delimiter: Char): String {
        return readUntil { it == delimiter }
    }

    private fun readUntil(stopFn: (Char) -> Boolean): String {
        val out = StringWriter()
        var char = reader.read()
        while (char > 0 && !stopFn(char.toChar())) {
            out.write(char)
            char = reader.read()
        }
        return out.toString()
    }

    fun skipWhiteSpaces() {
        skip { it.isWhitespace() }
    }

    private fun skip(continueFn: (Char) -> Boolean) {
        var char = reader.read()
        while (char > 0 && continueFn(char.toChar())) {
            char = reader.read()
        }
        reader.unread(char)
    }

    override fun close() {
        reader.close()
    }
}

fun main() {
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
    """.trimIndent()

    Parser(json).use {
        val result = it.parse()
        println(result)
    }
}