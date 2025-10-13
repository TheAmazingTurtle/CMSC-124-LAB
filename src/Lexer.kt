import kotlin.system.exitProcess

class Lexer() {
    private val lineScanner = Scanner()
    private var lineNumber = 0

    fun getNewLine (newlineString: String): Line{
        lineScanner.setLineString(newlineString)
        lineNumber++

        val tokenList = mutableListOf<Token>()

        while (lineScanner.hasCharsLeft()){
            val curChar = lineScanner.curChar ?: ' '
            val prevChar = lineScanner.prevChar ?: ' '
            val nextChar = lineScanner.nextChar

            if (curChar.isWhitespace()) {
                lineScanner.advance()
                continue
            }

            if (curChar == '/' && nextChar == '/') {
                break
            }

            if (curChar == '/' && nextChar == '*') {
                findCommentTerminator()
                continue
            }

            val token = when {
                            curChar == '"'      -> formString()
                            curChar == '\''     -> formChar()
                            curChar.isLetter()  -> formWord()
                            curChar.isDigit() || curChar == '.' && !prevChar.isLetterOrDigit() -> formNumber()
                            else                -> formSymbol()
                        }

            tokenList.add(token)
        }
        tokenList.add(Token("EOF", "", null, lineNumber))

        return Line(newlineString, lineNumber, tokenList)
    }

    private fun formString(): Token{
        val lexemeBuilder = StringBuilder()
        lexemeBuilder.append(lineScanner.curChar)
        lineScanner.advance()
        while (lineScanner.hasCharsLeft()){
            lexemeBuilder.append(lineScanner.curChar)

            if (lineScanner.curChar == '"'){
                lineScanner.advance()
                val lexeme = lexemeBuilder.toString()
                val literal = lexeme.substring(1, lexeme.length-1)
                return Token("STRING", lexeme, literal, lineNumber)
            }

            lineScanner.advance()
        }
        displayErrorMsg("SYNTAX", "STRING")
        return Token(null, "", null, lineNumber)
    }

    private fun formChar(): Token{
        lineScanner.advance()
        if (lineScanner.curChar == null || lineScanner.nextChar != '\''){
            displayErrorMsg("SYNTAX", "CHAR")
        }
        val literal = lineScanner.curChar ?: ' '
        lineScanner.advance(2)
        return Token("CHAR", "\'${literal}\'", literal, lineNumber)
    }

    private fun formWord(): Token{
        lineScanner.markStart()
        while (lineScanner.hasCharsLeft() && lineScanner.curChar?.isLetterOrDigit() == true) {
            lineScanner.advance()
        }

        val lexeme = lineScanner.getSubstring()

        if (lexeme == "true"){
            return Token("TRUE", "TRUE", 1, lineNumber)
        }

        if (lexeme == "false"){
            return Token("FALSE", "FALSE", 0, lineNumber)
        }

        return Token(identifyKeyword(lexeme), lexeme, null, lineNumber)
    }

    private fun formNumber(): Token {
        lineScanner.markStart()
        var type = "INT_NUMBER"
        var seenDot = false

        while (lineScanner.hasCharsLeft()) {
            val char = lineScanner.curChar ?: ' '

            when {
                char.isDigit()  -> {}    // skip
                char == '.'     ->  {
                                        if (seenDot) displayErrorMsg("SYNTAX", type)
                                        seenDot = true
                                        type = "FLOAT_NUMBER"
                                    }
                char.isLetter() -> displayErrorMsg("SYNTAX", type)
                else            -> break           // stops when meeting a symbol or whitespace
            }
            lineScanner.advance()
        }

        val lexeme = lineScanner.getSubstring()
        return  when (type) {
                    "INT_NUMBER"    -> Token(type, lexeme, lexeme.toInt(), lineNumber)
                    "FLOAT_NUMBER"  -> Token(type, lexeme, lexeme.toFloat(), lineNumber)
                    else            -> Token("UNIDENTIFIED_NUMBER", lexeme, lexeme, lineNumber)
                }
    }

    private fun formSymbol(): Token {
        lineScanner.markStart()

        val nextChar = lineScanner.nextChar
        val step = if (nextChar != null && !nextChar.isWhitespace() && !nextChar.isLetterOrDigit() && nextChar !in setOf('"', '\'', '.')) 2 else 1
        lineScanner.advance(step)

        val lexeme = lineScanner.getSubstring()
        return Token(identifySymbol(lexeme), lexeme, null, lineNumber)
    }

    private fun findCommentTerminator(){
        lineScanner.advance(2)
        while (lineScanner.hasCharsLeft()){
            if (lineScanner.curChar == '*' && lineScanner.nextChar == '/') {
                lineScanner.advance(2)  // skip over */
                return
            }
            lineScanner.advance()
        }
    }

    private fun identifySymbol(lexeme: String): String? {
        val symbolType = KeySymbol.entries.find { it.symbol == lexeme }?.name
        if (symbolType == null) {
            displayErrorMsg("SYNTAX","SYMBOL",lexeme)
        }

        return symbolType
    }

    private fun identifyKeyword(lexeme: String): String? {
        val wordType = Keyword.entries.find { it.word == lexeme }?.name
        if (wordType == null) {
            return "IDENTIFIER"
        }
        return wordType
    }

    private fun displayErrorMsg(errorType: String, tokenType: String, errorSymbol: String? = null){
        when{
            errorType == "SYNTAX" ->  identifySyntaxErrorMsg(tokenType, errorSymbol)
        }
        exitProcess(1)
    }

    private fun identifySyntaxErrorMsg(tokenType: String, errorSymbol: String?){
        val syntaxErrorMsg: String = "SyntaxError:"

        when (tokenType){
            "INT_NUMBER" -> println("$syntaxErrorMsg cannot start identifier with a number at line $lineNumber")
            "FLOAT_NUMBER" -> println("$syntaxErrorMsg improper number format at line $lineNumber")
            "SYMBOL" -> println("$syntaxErrorMsg unexpected symbol $errorSymbol at line $lineNumber")
            "STRING" -> println("$syntaxErrorMsg unterminated string at line $lineNumber")
            "CHAR" -> println("$syntaxErrorMsg improper char format at line $lineNumber")
        }
    }
}

