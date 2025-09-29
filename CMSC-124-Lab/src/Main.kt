import kotlin.collections.mutableListOf
import kotlin.system.exitProcess
import kotlin.text.isDigit
import kotlin.text.isLetter
import kotlin.text.isLetterOrDigit
import kotlin.text.isWhitespace

enum class KeySymbol(val symbol: String, ) {
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
    BOOLEAN_DATA_TYPE("boolean"),

    // METHOD
    PRINT_METHOD("print"),
    INPUT_METHOD("input")
}

class CodeFile(){
    private val fileLexer = Lexer()
    private val lineList = mutableListOf<Line>()

    fun addLine(content: String){
        val newLine = fileLexer.getNewLine(content)
        lineList.add(newLine)
    }

    fun displayTokens(lineNumber: Int){
        for (token in lineList[lineNumber-1].tokenList){
            println("Token(type=${token.type}, lexeme=${token.lexeme}, literal=${token.literal}, line=${token.lineNumber})")
        }
    }

    fun displayAllTokens(){
        for (line in lineList){
            for (token in line.tokenList){
                println("Token(type=${token.type}, lexeme=${token.lexeme}, literal=${token.literal}, line=${token.lineNumber})")
            }
        }
    }
}

data class Line(
    val content: String,
    val number: Int,
    val tokenList: List<Token>
)

data class Token(
    val type: String?,
    val lexeme: String,
    val literal: Any?,
    val lineNumber: Int
)

fun main() {
    val mainFile = CodeFile()

    print(">>")
    val user_input = readLine() ?:""

    mainFile.addLine(user_input)
    mainFile.displayTokens(1)
}
