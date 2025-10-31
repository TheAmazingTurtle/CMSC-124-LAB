import javax.xml.transform.Source

class Lexer {
    private var sourceLine = ""
    private var index = 0
    private var lineNumber = 0
    private var errorMsg: String? = null

    fun getTokensFromLine (userInput: String, lineNumber: Int): List<Token> {
        setupLexer(userInput, lineNumber)
        val tokenList = mutableListOf<Token>()

        while (hasMoreChars()){
            if (getCurChar().isWhitespace()) {
                consumeChar()
                continue
            }

            if (getCurChar() == '/' && getNextChar() == '/') {
                break
            }

            if (getCurChar() == '/' && getNextChar() == '*') {
                handleMultilineComment()
                continue
            }

            if (getCurChar() == '.' && !getPrevChar().isLetterOrDigit()){
                val token = formNumberToken()
                tokenList.add(token)
            }

            val token = when {
                getCurChar() == '\"'                                    -> formStringToken()
                getCurChar() == '\''                                    -> formCharToken()
                getCurChar().isLetter()                                 -> formIdentifierToken()
                getCurChar().isDigit()                                  -> formNumberToken()
                getCurChar() == '.' && !getPrevChar().isLetterOrDigit() -> formNumberToken()
                else                                                    -> formSymbolToken()
            }

            tokenList.add(token)
        }

        return tokenList.toList()
    }

    private fun setupLexer(sourceLine: String, lineNumber: Int) {
        this.sourceLine = sourceLine
        this.lineNumber = lineNumber
        index = 0
    }

    private fun getPrevChar(): Char {
        return sourceLine.getOrNull(index + 1) ?: ' '
    }

    private fun getCurChar(): Char {
        return sourceLine[index]
    }

    private fun getNextChar(): Char? {
        return sourceLine.getOrNull(index + 1)
    }

    private fun peek(index: Int): Char? {
        return sourceLine.getOrNull(index)
    }

    private fun consumeChar() {
        index++
    }

    private fun hasMoreChars(): Boolean {
        return index < sourceLine.length
    }
}