import kotlin.system.exitProcess

enum class Operator(val symbol: String) {
    LESSER("<"),
    GREATER(">"),
    LESSER_EQUAL("<="),
    GREATER_EQUAL(">="),
    EQUAL("=="),
    NOT("!"),
    AND("&&"),
    OR("||"),

    ASSIGN("="),

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    POWER ("**"),

    //COMPOUND OPERATORS
    INCREMENT("++"),
    DECREMENT("--"),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    DIVIDE_ASSIGN("/="),
    MODULO_ASSIGN("%="),
}

enum class Delimiter(val symbol: String) {
    L_PAREN("("),
    R_PAREN(")"),
    L_BRACKET("["),
    R_BRACKET("]"),
    L_BRACE("{"),
    R_BRACE("}"),
    COMMA(",")
}

enum class PrimitiveType(val keyword: String) {
    INT("int"),
    CHAR("char"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("String"),
    BOOLEAN("boolean")
}


val operators = Operator.entries.map { it.symbol }.toSet()
val delimiters = Delimiter.entries.map { it.symbol}.toSet()
val data_types = PrimitiveType.entries.map { it.keyword }.toSet()


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
            val nextChar: Char? = content.getOrNull(index + 1)

            when {
                char == '/' -> {                        // comment check
                    when (nextChar) {
                        '/' -> break
                        '*' -> checkBlockComment()
                        else -> formSymbol()
                    }
                }
                char.isLetter() -> formWord()
                char.isDigit() -> formNumber()
                char.isWhitespace() -> {}               // skip
                else -> formSymbol()
            }
            index++
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
        val type = "SYMBOL"
        val start = index

        val nextChar = content.getOrNull(index + 1)
        if (nextChar != null && !nextChar.isWhitespace() && !nextChar.isLetterOrDigit()){
            index++
        }

        val stringedSymbol = content.substring(start, index)

        if (identifySymbol(stringedSymbol) != null) {
            tokenize(stringedSymbol, type)
        }
        else {
            displayErrorMsg("SYNTAX", type, stringedSymbol)
        }
    }

    fun checkBlockComment() {
        index += 2
        while (index + 1 < content.length){
            if (content[index] == '*' && content[index+1] == '/'){
                index += 2
                return
            }
            index++
        }
        displayErrorMsg("SYNTAX", "BLOCK_COMMENT",null)
    }

    fun identifySymbol(lexeme: String): String?{
        return Operator.entries.find { it.symbol == lexeme }?.name
            ?: Delimiter.entries.find { it.symbol == lexeme }?.name
    }

    fun tokenize(lexeme: String, type: String? = null){
        when{
            type == "INT_NUMBER"    -> tokens.add(Token(type, lexeme, lexeme))
            type == "FLOAT_NUMBER"  -> tokens.add(Token(type, lexeme, if (lexeme.last() == '.') "${lexeme}0" else lexeme))
            type == "SYMBOL"        -> tokens.add(Token(identifySymbol(lexeme) ?: displayErrorMsg("SYNTAX",type,lexeme), lexeme, null))
            lexeme in data_types    -> tokens.add(Token("DATA_TYPE", lexeme, null))
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

data class Token(val type: Any, val lexeme: String, val literal: String?)

fun main() {
    val mainFile = CodeFile()

    print(">>")
    val user_input = readLine() ?:""

    mainFile.add(user_input)

}
