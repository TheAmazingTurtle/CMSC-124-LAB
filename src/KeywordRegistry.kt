object KeywordRegistry {
    private val arithmeticKeywords = mapOf(
        "added" to TokenType.ADDED,
        "subtracted" to TokenType.SUBTRACTED,
        "multiplied" to TokenType.MULTIPLIED,
        "divided" to TokenType.DIVIDED,
        "by" to TokenType.BY
    )

    private val logicKeywords = mapOf(
        "and" to TokenType.AND,
        "or" to TokenType.OR,
        "not" to TokenType.NOT,
        "is" to TokenType.IS,
        "equal" to TokenType.EQUAL,
        "greater" to TokenType.GREATER,
        "less" to TokenType.LESS,
        "than" to TokenType.THAN,
        "true" to TokenType.BOOLEAN,
        "false" to TokenType.BOOLEAN
    )

    private val assignKeywords = mapOf(
        "set" to TokenType.SET,
        "to" to TokenType.TO,
        "as" to TokenType.AS
    )

    private val printKeyword = mapOf(
        "show" to TokenType.SHOW
    )

    private val conditionalKeywords = mapOf(
        "if" to TokenType.IF,
        "then" to TokenType.THEN,
        "otherwise" to TokenType.OTHERWISE,
        "end" to TokenType.END,
    )

    private val loopKeywords = mapOf(
        "while" to TokenType.WHILE,
        "do" to TokenType.DO,
        "for" to TokenType.FOR,
    )

    private val switchKeywords = mapOf(
        "based" to TokenType.BASED,
        "when" to TokenType.WHEN,
        "escape" to TokenType.ESCAPE
    )


    private val allKeywords = arithmeticKeywords + logicKeywords + assignKeywords + printKeyword + conditionalKeywords + loopKeywords + switchKeywords


    fun getWordType(word: String): TokenType? {
        return allKeywords[word]
    }
}
