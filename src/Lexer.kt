class Lexer {
    private var sourceLine = ""
    private var index = 0
    private var lineNumber = 0
    private var errorMsg: String? = null
    private var isMultilineCommentActive = false

    fun getTokensFromLine (userInput: String, lineNumber: Int): List<Token?> {
        setupLexer(userInput, lineNumber)
        val tokenList = mutableListOf<Token?>()

        if (isMultilineCommentActive){
            enforceMultilineComment()
        }

        while (hasMoreChars()){
            if (getCurChar().isWhitespace()) {
                consumeChar()
                continue
            }

            if (getCurChar() == '#') { // line-comment
                break
            }

            if (getCurChar() == '*' && getNextChar() == '*' && peek(index+2) == '*') { // multiline-comment
                isMultilineCommentActive = true
                consumeChar(3)
                enforceMultilineComment()
                continue
            }

            val token = when {
                getCurChar().isLetter()                                 -> formWordToken()
                getCurChar().isDigit()                                  -> formNumberToken()
                getCurChar() == '\"' || getCurChar() == '\''            -> formStringToken()
                getCurChar() == '.' && getNextChar().isDigit()          -> formNumberToken()
                else                                                    -> formSymbolToken()
            }

            tokenList.add(token)
        }

        val endOfLineToken = Token(TokenType.EOL, "", null, lineNumber)
        tokenList.add(endOfLineToken)

        return tokenList.toList()
    }

    fun isErrorFound(): Boolean = errorMsg != null
    fun getErrorMsg(): String? = errorMsg

    private fun formStringToken(): Token? {
        val terminator = getCurChar()
        consumeChar()

        val literalStartingIndex = index

        while (hasMoreChars() && getCurChar() != terminator) {
            consumeChar()
        }

        if (!hasMoreChars() || getCurChar() != terminator) {
            raiseLexError("Unterminated string")
            return null
        }

        val literal = sourceLine.substring(literalStartingIndex, index)
        val stringToken = Token(TokenType.STRING, "$terminator$literal$terminator", literal, lineNumber)
        consumeChar()

        return stringToken
    }

    private fun formWordToken(): Token {
        val lexemeStartingIndex = index

        while (hasMoreChars() && getCurChar().isLetterOrDigit()){
            consumeChar()
        }

        val lexeme = sourceLine.substring(lexemeStartingIndex, index)
        val wordType = KeywordRegistry.getWordType(lexeme) ?: TokenType.IDENTIFIER
        val literal = if (wordType == TokenType.TRUE || wordType == TokenType.FALSE) lexeme.toBoolean() else null
        val wordToken = Token(wordType, lexeme, literal, lineNumber)

        return wordToken
    }

    private fun formNumberToken(): Token? {
        val lexemeStartingIndex = index
        var seenDot = false

        while (hasMoreChars()) {
            when {
                getCurChar().isLetter() -> {
                    raiseLexError("Identifier starts with a number")
                    return null
                }
                getCurChar().isDigit()  -> {}    // skip
                getCurChar() == '.'     ->  {
                    if (seenDot) {
                        raiseLexError("Improper number format")
                        return null
                    }
                    seenDot = true
                }
                else            -> break           // stops when meeting a symbol or whitespace
            }
            consumeChar()
        }

        val lexeme = sourceLine.substring(lexemeStartingIndex, index)
        val literal = if (seenDot) lexeme.toFloat() else lexeme.toInt()
        val numberToken = Token(TokenType.NUMBER, lexeme, literal, lineNumber)

        return numberToken
    }

    private fun formSymbolToken(): Token? {
        val lexemeStartingIndex = index

        while (hasMoreChars() && isSymbol(getCurChar())){
            consumeChar()
        }

        val lexeme = sourceLine.substring(lexemeStartingIndex, index)
        val symbolType = SymbolRegistry.getSymbolType(lexeme)

        if (symbolType == null){
            raiseLexError("Unrecognized symbol")
            return null
        }

        val symbolToken = Token(symbolType, lexeme, null, lineNumber)
        return symbolToken
    }

    private fun enforceMultilineComment() {
        while (hasMoreChars() && isMultilineCommentActive) {
            when {
                getCurChar() != '*'     -> consumeChar()
                getNextChar() != '*'    -> consumeChar(2)
                peek(index+2) != '*'    -> consumeChar(3)
                else -> {
                    consumeChar(3)
                    isMultilineCommentActive = false
                }
            }
        }

    }

    private fun setupLexer(sourceLine: String, lineNumber: Int) {
        this.sourceLine = sourceLine
        this.lineNumber = lineNumber
        this.errorMsg = null
        index = 0
    }

    private fun getCurChar(): Char = sourceLine[index]
    private fun getNextChar(): Char = sourceLine.getOrNull(index + 1) ?: ' '
    private fun peek(index: Int): Char? = sourceLine.getOrNull(index)
    private fun hasMoreChars(): Boolean = index < sourceLine.length

    private fun consumeChar(numOfCharToConsume: Int = 1) {
        index += numOfCharToConsume
    }

    private fun isSymbol(char: Char): Boolean {
        return when {
            char.isLetterOrDigit()  -> false
            char.isWhitespace()     -> false
            char == '\''            -> false
            char == '\"'            -> false
            else                    -> true
        }
    }

    private fun raiseLexError(errorMsg: String) {
        this.index = sourceLine.length
        this.errorMsg = "Error: $errorMsg at line $lineNumber"
    }
}