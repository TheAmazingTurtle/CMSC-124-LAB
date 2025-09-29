import kotlin.system.exitProcess

enum class KeySymbol(val symbol: String) {
    // Logical Operators
    LESSER("<"),
    GREATER(">"),
    LESSER_EQUAL("<="),
    GREATER_EQUAL(">="),
    EQUAL("=="),
    NOT("!"),
    AND("&&"),
    OR("||"),

    // Assignment Operators
    ASSIGN("="),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    DIVIDE_ASSIGN("/="),
    MODULO_ASSIGN("%="),

    // Arithmetic Operators
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    POWER ("**"),

    // INCR/DECR
    INCREMENT("++"),
    DECREMENT("--"),

    // Delimiters
    L_PAREN("("),
    R_PAREN(")"),
    L_BRACKET("["),
    R_BRACKET("]"),
    L_BRACE("{"),
    R_BRACE("}"),
    COMMA(","),
    DOT("."),

    // Comment
    ONE_LINE_COMMENT("//"),
    MULTI_LINE_COMMENT_OPEN("/*"),
    MULTI_LINE_COMMENT_CLOSE("/*")
}

enum class Keyword(val word: String)  {
    // DATA_TYPE
    INT_DATA_TYPE("int"),
    CHAR_DATA_TYPE("char"),
    FLOAT_DATA_TYPE("float"),
    DOUBLE_DATA_TYPE("double"),
    STRING_DATA_TYPE("String"),
    BOOLEAN_DATA_TYPE("boolean")

    // method

}

val keySymbolMap = KeySymbol.entries.associateBy { it.symbol }
fun isSymbol(lexeme: String): Boolean = keySymbolMap.containsKey(lexeme)

val keywordMap = Keyword.entries.associateBy { it.word }
fun isKeyword(lexeme: String): Boolean = keywordMap.containsKey(lexeme)


class CodeFile(){
    val lines = mutableListOf<Line>()

    fun add(line: String){
        lines.add(Line(line, lines.size + 1))
    }
}

class Line(val content: String, val lineNum: Int){
    val tokens = mutableListOf<Token>()
    var index = 0

    init {
        constructTokens()
        displayTokens()
    }

    fun constructTokens(){
        while (index < content.length){
            val char = content[index]
            val nextChar = content.getOrNull(index+1)

            when {
                char == '/' && nextChar == '/'  -> break
                char == '/' && nextChar == '*'  -> findCommentTerminator()
                char == '"'                     -> formString()
                char.isLetter()                 -> formWord()
                char.isDigit()                  -> formNumber()
                char.isWhitespace()             -> index++                              // skip
                else                            -> formSymbol()
            }
        }
        tokens.add(Token("EOF", "", null))
    }

    fun formString(){
        index++
        val formedString = StringBuilder()
        while (index < content.length){
            val char = content.getOrNull(index++)

            if (char == '"'){
                tokenize(formedString.toString(), "STRING")
                return
            } else {
                formedString.append(char)
            }
        }

        displayErrorMsg("SYNTAX", "STRING", null)

    }

    fun formWord(){
        val start = index
        while (index < content.length && content[index].isLetterOrDigit()) {
            index++
        }
        val word = content.substring(start, index)
        tokenize(word)
    }

    fun formNumber() {
        val start = index
        var type = "INT_NUMBER"
        var seenDot = false

        while (index < content.length) {
            val char = content[index]

            when {
                char.isDigit()  -> {}    // skip
                char == '.'     ->  {
                                        if (seenDot) displayErrorMsg("SYNTAX", type, null)
                                        seenDot = true
                                        type = "FLOAT_NUMBER"
                                    }
                char.isLetter() -> displayErrorMsg("SYNTAX", type, null)
                else -> break           // stops when meeting a symbol or whitespace
            }
            index++
        }

        val number = content.substring(start, index)
        tokenize(number, type)
    }

    fun formSymbol() {
        val start = index

        index++
        val nextChar = content.getOrNull(index)
        if (nextChar != null && !nextChar.isWhitespace() && !nextChar.isLetterOrDigit()){
            index++
        }

        val stringedSymbol = content.substring(start, index)
        tokenize(stringedSymbol)
    }

    fun findCommentTerminator(){
        index += 2
        while (index < content.length){
            if (content[index] == '*' && content.getOrNull(index + 1) == '/') {
                index += 2  // skip over */
                return
            }
            index++
        }
    }

    fun identifySymbol(lexeme: String): String? {
        val symbolType = KeySymbol.entries.find { it.symbol == lexeme }?.name

        if (symbolType == null) {
            displayErrorMsg("SYNTAX","SYMBOL",lexeme)
        }

        return symbolType
    }

    fun identifyKeyword(lexeme: String): String? {
        val wordType = Keyword.entries.find { it.word == lexeme }?.name

        return wordType
    }

    fun tokenize(lexeme: String, type: String? = null){
        tokens.add (
            when{
                type == "INT_NUMBER"    -> Token(type, lexeme, lexeme)
                type == "FLOAT_NUMBER"  -> Token(type, lexeme, if (lexeme.last() == '.') "${lexeme}0" else lexeme)
                type == "STRING"        -> Token(type, "\"${lexeme}\"", lexeme)
                isSymbol(lexeme)        -> Token(identifySymbol(lexeme), lexeme, null)
                isKeyword(lexeme)       -> Token(identifyKeyword(lexeme), lexeme, null)
                else                    -> Token("IDENTIFIER", lexeme, null)
            }
        )
    }

    fun identifyTokenType(){

    }

    fun displayTokens(){
        for (token in tokens){
            println("Token(type=${token.type}, lexeme=${token.lexeme}, literal=${token.literal}, line=$lineNum)")
        }
    }


    fun displayErrorMsg(errorType: String, tokenType: String, errorSymbol: String?){
        when{
            errorType == "SYNTAX" ->  identifySyntaxErrorMsg(tokenType, errorSymbol)
        }
        exitProcess(1)
    }

    fun identifySyntaxErrorMsg(tokenType: String, errorSymbol: String?){
        val SyntaxErrorMsg: String = "SyntaxError:"
        when (tokenType){
            "INT_NUMBER" -> println("$SyntaxErrorMsg cannot start identifier with a number at line $lineNum")
            "FLOAT_NUMBER" -> println("$SyntaxErrorMsg improper number format at line $lineNum")
            "SYMBOL" -> println("$SyntaxErrorMsg unexpected symbol $errorSymbol at line $lineNum")
            "STRING" -> println("$SyntaxErrorMsg unterminated string at line $lineNum")
            "BLOCK_COMMENT" -> println("$SyntaxErrorMsg unterminated block comment")
        }
    }
}

data class Token(val type: String?, val lexeme: String, val literal: String?)

fun main() {
    val mainFile = CodeFile()

    print(">>")
    val user_input = readLine() ?:""

    mainFile.add(user_input)

}
