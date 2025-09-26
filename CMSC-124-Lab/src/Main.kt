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
    EXPONENT ("^"),

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


class File(){
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
            var char = content[index]
            //var nextChar: Char? = content.getOrNull(index + 1)

            when {
                char == '/' && content.getOrNull(index+1)=='/' ->{
                    index = content.length
                    break
                }
                char.isLetter() -> formWord()
                char.isDigit() -> formNumber()
                char.isWhitespace() -> Unit
                    //print("$char is whitespace\n")

                else -> formSymbol()
            }
            index++
        }
        tokens.add(Token("EOF", "", null))
    }

    fun formWord() {
        val start = index
        while (index < content.length){
            if (content[index].isWhitespace() || content[index].toString() in operators + delimiters){
                index--
                tokenize(content.slice(start..index))
                return
            }

            if (!(content[index].isLetter() || content[index].isDigit())) displayErrorMsg("SYNTAX", "DATA_TYPE", null)
            index++
        }
        index--
        tokenize(content.slice(start..index))
        return
    }

    fun formNumber() {
        val start = index
        var type = "INT_NUMBER"

        while (index < content.length){
            if (!(content[index].isLetter() || content[index].isDigit() || content[index] == '.')){
                index--
                tokenize(content.slice(start..index), type)
                return
            }

            if (content[index] == '.' && type == "INT_NUMBER"){
                type = "FLOAT_NUMBER"
            } else if (content[index] == '.') {
                displayErrorMsg("SYNTAX", type,null)
            }

            if (content[index].isLetter()) displayErrorMsg("SYNTAX", type, null)
            index++
        }
        index--
        tokenize(content.slice(start..index), type)
        return
    }

    fun formSymbol() {
        val type = "SYMBOL"
        var symbol = StringBuilder()
        while (index < content.length && symbol.length < 2) {
            if ((content[index].isWhitespace() || content[index].isLetter() || content[index].isDigit())) {
                index--
                break
            }
            symbol.append(content[index])
            index++
        }

        val stringedSymbol = symbol.toString()
        if (identifySymbol(stringedSymbol) != null){
            tokenize(stringedSymbol, type)
            return
        } else {
            displayErrorMsg("SYNTAX", type, stringedSymbol)
        }
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
            tokenType == "DATA_TYPE" -> println("$SyntaxErrorMsg variables cannot contain symbols at line $lineNum")
            tokenType == "INT_NUMBER" -> println("$SyntaxErrorMsg cannot start identifier with a number at line $lineNum")
            tokenType == "FLOAT_NUMBER" -> println("$SyntaxErrorMsg improper number format at line $lineNum")
            tokenType == "SYMBOL" -> println("$SyntaxErrorMsg unexpected symbol $errorSymbol at line $lineNum")
        }
    }
}

data class Token(val type: Any, val lexeme: String, val literal: String?)

fun main() {
    val mainFile = File()

    print(">>")
    val user_input = readLine() ?:""

    mainFile.add(user_input)


}

