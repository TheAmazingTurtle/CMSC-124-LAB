import kotlin.system.exitProcess

sealed interface TokenType

enum class KeySymbol(val symbol: String, val tokenCategory: TokenCategory): TokenType {
    // Logical Operators
    LESSER("<", TokenCategory.LOGIC_OPERATOR),
    GREATER(">", TokenCategory.LOGIC_OPERATOR),
    LESSER_EQUAL("<=", TokenCategory.LOGIC_OPERATOR),
    GREATER_EQUAL(">=", TokenCategory.LOGIC_OPERATOR),
    EQUAL("==", TokenCategory.LOGIC_OPERATOR),
    NOT("!", TokenCategory.LOGIC_OPERATOR),
    AND("&&", TokenCategory.LOGIC_OPERATOR),
    OR("||", TokenCategory.LOGIC_OPERATOR),

    // Assignment Operators
    ASSIGN("=", TokenCategory.ASSIGN_OPERATOR),
    PLUS_ASSIGN("+=", TokenCategory.ASSIGN_OPERATOR),
    MINUS_ASSIGN("-=", TokenCategory.ASSIGN_OPERATOR),
    DIVIDE_ASSIGN("/=", TokenCategory.ASSIGN_OPERATOR),
    MODULO_ASSIGN("%=", TokenCategory.ASSIGN_OPERATOR),

    // Arithmetic Operators
    PLUS("+", TokenCategory.ARITHMETIC_OPERATOR),
    MINUS("-", TokenCategory.ARITHMETIC_OPERATOR),
    MULTIPLY("*", TokenCategory.ARITHMETIC_OPERATOR),
    DIVIDE("/", TokenCategory.ARITHMETIC_OPERATOR),
    MODULO("%", TokenCategory.ARITHMETIC_OPERATOR),
    POWER ("**", TokenCategory.ARITHMETIC_OPERATOR),

    // INCR/DECR
    INCREMENT("++", TokenCategory.INCREMENT),
    DECREMENT("--", TokenCategory.INCREMENT),

    // Delimiters
    L_PAREN("(", TokenCategory.DELIMITER),
    R_PAREN(")", TokenCategory.DELIMITER),
    L_BRACKET("[", TokenCategory.DELIMITER),
    R_BRACKET("]", TokenCategory.DELIMITER),
    L_BRACE("{", TokenCategory.DELIMITER),
    R_BRACE("}", TokenCategory.DELIMITER),
    COMMA(",", TokenCategory.DELIMITER),
    DOT(".", TokenCategory.DELIMITER),

    // Comment
    ONE_LINE_COMMENT("//", TokenCategory.COMMENT),
    MULTI_LINE_COMMENT("/*", TokenCategory.COMMENT)
}

enum class Keyword(val word: String, val tokenCategory: TokenCategory): TokenType  {
    // DATA_TYPE
    INT_DATA_TYPE("int", TokenCategory.DATA_TYPE),
    CHAR_DATA_TYPE("char", TokenCategory.DATA_TYPE),
    FLOAT_DATA_TYPE("float", TokenCategory.DATA_TYPE),
    DOUBLE_DATA_TYPE("double", TokenCategory.DATA_TYPE),
    STRING_DATA_TYPE("String", TokenCategory.DATA_TYPE),
    BOOLEAN_DATA_TYPE("boolean", TokenCategory.DATA_TYPE)
}

enum class TokenCategory {
    ARITHMETIC_OPERATOR, LOGIC_OPERATOR, ASSIGN_OPERATOR, DELIMITER, INCREMENT, COMMENT,
    DATA_TYPE
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
    var oneLineCommentFound = false
    var multiLineCommentActive = false

    init {
        constructTokens()
        displayTokens()
    }

    fun constructTokens(){
        while (index < content.length){
            val char = content[index]
            val nextChar: Char? = content.getOrNull(index + 1)

            when {
                oneLineCommentFound -> break
                multiLineCommentActive -> findCommentTerminator()
                char.isLetter() -> formWord()
                char.isDigit() -> formNumber()
                char.isWhitespace() -> index++                              // skip
                else -> formSymbol()
            }
        }
        tokens.add(Token("EOF", "", null))
    }

    fun formWord() {
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
                char.isDigit() -> {}    // skip
                char == '.' -> {
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
        println(stringedSymbol)
        tokenize(stringedSymbol)
    }

    fun identifySymbol(lexeme: String) {
        val symbolType = KeySymbol.entries.find { it.symbol == lexeme }?.name

        when {
            symbolType == null                  -> displayErrorMsg("SYNTAX","SYMBOL",lexeme)
            symbolType == "ONE_LINE_COMMENT"    -> oneLineCommentFound = true
            symbolType == "MULTI_LINE_COMMENT"  -> multiLineCommentActive = !multiLineCommentActive
            else                                -> tokens.add(Token(symbolType, lexeme, null))
        }
    }

    fun findCommentTerminator(){
        val start = index

        index++
        val nextChar = content.getOrNull(index)
        if (nextChar != null && !nextChar.isWhitespace() && !nextChar.isLetterOrDigit()){
            index++
        }

        val stringedSymbol = content.substring(start, index)
        val symbolType = KeySymbol.entries.find { it.symbol == stringedSymbol }?.name

        if (symbolType == "MULTI_LINE_COMMENT") {
            multiLineCommentActive = !multiLineCommentActive
        }
    }

    fun identifyKeyword(lexeme: String){
        val wordType = Keyword.entries.find { it.word == lexeme }?.name
        tokens.add(Token(wordType, lexeme, null))
    }

    fun tokenize(lexeme: String, type: String? = null){
        when{
            type == "INT_NUMBER"    -> tokens.add(Token(type, lexeme, lexeme))
            type == "FLOAT_NUMBER"  -> tokens.add(Token(type, lexeme, if (lexeme.last() == '.') "${lexeme}0" else lexeme))
            isSymbol(lexeme)        -> identifySymbol(lexeme)
            isKeyword(lexeme)       -> identifyKeyword(lexeme)
            else -> tokens.add(Token("IDENTIFIER", lexeme, null))
        }
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
        when{
            tokenType == "INT_NUMBER" -> println("$SyntaxErrorMsg cannot start identifier with a number at line $lineNum")
            tokenType == "FLOAT_NUMBER" -> println("$SyntaxErrorMsg improper number format at line $lineNum")
            tokenType == "SYMBOL" -> println("$SyntaxErrorMsg unexpected symbol $errorSymbol at line $lineNum")
            tokenType == "BLOCK_COMMENT" -> println("$SyntaxErrorMsg unterminated block comment")
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
