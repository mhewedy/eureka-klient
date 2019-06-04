package heplers.json

// 1: writing tokenizer
// 2: building AST
// 3: convert AST to object graph

object Token {
    const val leftBracket = "["
    const val rightBracket = "]"
    const val leftBrace = "{"
    const val rightBrace = "}"
    const val doubleQuote = "\""
    const val comma = ","
    const val colon = ":"

    //todo add all tokens in here: http://json.org/
}

fun main() {
    println(Token.leftBracket)
}