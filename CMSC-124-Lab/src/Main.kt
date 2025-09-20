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
    MODULO("%")
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
val data_types = PrimitiveType.entries.map { it.keyword }.toSet()


class File(){
    val lines = mutableListOf<Line>()

    fun add(line: String){
        lines.add(Line(line, lines.size + 1))
    }
}

class Line(val content: String, val line_num: Int){
    val tokens = mutableListOf<Token>()
    var index = 0

    init {
        constructTokens()
        displayTokens()
    }

    fun constructTokens(){
        while (index < content.length){
            var char = content[index]
            var next_char: Char? = content.getOrNull(index + 1)

            when {
                char.isLetter() -> formWord()
                char.isDigit() -> formNumber()
                char.isWhitespace() -> print("$char is whitespace")
                else -> formSymbol()
            }

            index++
        }
    }

    fun formWord() {
        val start = index
        while (index < content.length){
            if (content[index].isWhitespace() || content[index].toString() in operators){
                index--
                tokenize(content.slice(start..index))
                return
            }

            if (!(content[index].isLetter() || content[index].isDigit())) throw IllegalArgumentException("Variables cannot contain symbols.")
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
                throw IllegalArgumentException("Improper number format.")
            }

            if (content[index].isLetter()) throw IllegalArgumentException("Identifiers cannot start with a number.")
            index++
        }
        index--
        tokenize(content.slice(start..index), type)
        return
    }

    fun formSymbol() {
        val start = index
        while (index < content.length){
            if (content[index].isWhitespace() || content[index].isLetter() || content[index].isDigit()){
                index--
                tokenize(content.slice(start..index), "SYMBOL")
                return
            }

            index++
        }
        index--
        tokenize(content.slice(start..index), "SYMBOL")
        return
    }

    fun identifySymbol(lexeme: String): String?{
        return Operator.entries.find { it.symbol == lexeme }?.name
    }

    fun tokenize(lexeme: String, type: String? = null){
        when{
            type == "INT_NUMBER"    -> tokens.add(Token(type, lexeme, lexeme))
            type == "FLOAT_NUMBER"  -> tokens.add(Token(type, lexeme, if (lexeme.last() == '.') "${lexeme}0" else lexeme))
            type == "SYMBOL"        -> tokens.add(Token(identifySymbol(lexeme) ?:"UNIDENTIFIED_SYMBOL", lexeme, null))
            lexeme in data_types    -> tokens.add(Token("DATA_TYPE", lexeme, null))
            else -> tokens.add(Token("IDENTIFIER", lexeme, null))
        }
    }

    fun displayTokens(){
        for (token in tokens){
            println("Token(type=${token.type}, lexeme=${token.lexeme}, literal=${token.literal}, line=$line_num)")
        }
    }
}

data class Token(val type: String, val lexeme: String, val literal: String?)

fun main() {
    val mainFile = File()

    print("Enter one line of code: ")
    val user_input = readLine() ?:""

    mainFile.add(user_input)


}

//when {
//    char.isLetter() -> print("$char is a letter")
//    char.isDigit() -> print("$char is a digit")
//    char.isWhitespace() -> print("$char is whitespace")
//    else -> print("$char is a symbol")
//}
//
//println(" | Next is $next_char")