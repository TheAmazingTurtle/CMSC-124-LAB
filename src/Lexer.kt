class Lexer {
    private var sourceLine = ""
    private var index = 0
    private var lineNumber = 0
    private var isMultilineCommentActive = false

    fun getTokensFromLine(sourceLine: String, lineNumber: Int = 1, multilineCommentPersists: Boolean = false): List<Token> {
        this.index = 0
        this.sourceLine = sourceLine
        this.lineNumber = lineNumber

        if (!multilineCommentPersists)
            this.isMultilineCommentActive = false

        val tokenList = mutableListOf<Token>()

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
                getCurChar() == '$' || getCurChar().isLetter() || getCurChar() == '_' -> formWordToken()
                getCurChar().isDigit()                                  -> formNumberToken()
                getCurChar() == '\"' || getCurChar() == '\''            -> formStringToken()
                getCurChar() == '.' && getNextChar().isDigit()          -> formNumberToken()
                else                                                    -> formSymbolToken()
            }

            tokenList.add(token)
        }

        val endOfLineToken = Token(TokenType.EOL, "", "null", lineNumber)
        tokenList.add(endOfLineToken)

        return tokenList.toList()
    }

//    fun isErrorFound(): Boolean = errorMsg != null
//    fun getErrorMsg(): String? = errorMsg

    private fun formStringToken(): Token {
        val terminator = getCurChar()
        consumeChar()

        val literalStartingIndex = index

        while (hasMoreChars() && getCurChar() != terminator) {
            consumeChar()
        }

        if (!hasMoreChars() || getCurChar() != terminator) throw Exception(createErrorMsg("Unterminated string"))

        val literal = sourceLine.substring(literalStartingIndex, index)
        val stringToken = Token(TokenType.STRING, "$terminator$literal$terminator", literal, lineNumber)
        consumeChar()

        return stringToken
    }

    private fun formWordToken(): Token {
        val lexemeStartingIndex = index

        while (hasMoreChars() && (getCurChar().isLetterOrDigit() || getCurChar() == '_' || getCurChar() == '$')){
            consumeChar()
        }

        val lexeme = sourceLine.substring(lexemeStartingIndex, index)
        val wordType = KeywordRegistry.getWordType(lexeme) ?: TokenType.IDENTIFIER
        val literal = if (wordType == TokenType.BOOLEAN) lexeme.toBoolean() else "null"
        val wordToken = Token(wordType, lexeme, literal, lineNumber)

        return wordToken
    }


    private fun formNumberToken(): Token {
        val lexemeStartingIndex = index
        var seenDot = false

        while (hasMoreChars()) {
            when {
                getCurChar().isLetter() -> {
                    var denomination = ""
                    for (i in 0..2){
                        if(!hasMoreChars()) throw Exception(createErrorMsg("Invalid currency code"))
                        denomination += getCurChar()
                        consumeChar()
                    }

                    if(hasMoreChars() && !getCurChar().isWhitespace()) throw Exception(createErrorMsg("Invalid currency code"))
                    val type = KeywordRegistry.getCurrencyType(denomination) ?:  throw Exception(createErrorMsg("Invalid denomination"))
                    val codeLexeme = sourceLine.substring(lexemeStartingIndex, index)
                    val amountLexeme = sourceLine.substring(lexemeStartingIndex, index - 3)
                    val literal =  if (seenDot) amountLexeme.toDouble() else amountLexeme.toDouble()
                    val rate = CurrencyRate.getCurrencyMultiplier(type) ?: throw Exception(createErrorMsg("Unknown currency code"))
                    val convertedLexeme = literal * rate
                    val currencyToken = Token(type, codeLexeme, convertedLexeme, lineNumber)
                    return currencyToken
                }
                getCurChar().isDigit()  -> {}    // skip
                getCurChar() == '.'     -> {
                    if (seenDot) throw Exception(createErrorMsg("Improper number format"))
                    seenDot = true
                }
                else            -> break           // stops when meeting a symbol or whitespace
            }
            consumeChar()
        }

        val lexeme = sourceLine.substring(lexemeStartingIndex, index)
        val literal = if (seenDot) lexeme.toDouble() as Number else lexeme.toInt() as Number
        val numberToken = Token(TokenType.NUMBER, lexeme, literal, lineNumber)

        return numberToken
    }

    private fun formSymbolToken(): Token {
        val lexemeStartingIndex = index

        while (hasMoreChars() && isSymbol(getCurChar())){
            consumeChar()
        }

        val lexeme = sourceLine.substring(lexemeStartingIndex, index)
        val symbolType = SymbolRegistry.getSymbolType(lexeme)
            ?: throw Exception(createErrorMsg("Unrecognized symbol"))

        val symbolToken = Token(symbolType, lexeme, "null", lineNumber)
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
            char == '$'             -> false
            else                    -> true
        }
    }

//    private fun raiseLexError(errorMsg: String) {
//        this.errorMsg = "Scanning Error: $errorMsg at line $lineNumber"
//
//        println("Scanning Error: $errorMsg at line $lineNumber")
//    }

    private fun createErrorMsg(errorContent: String): String = "Scanning Error: $errorContent at line $lineNumber"

}