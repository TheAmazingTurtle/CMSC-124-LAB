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

    private val allKeywords = arithmeticKeywords + logicKeywords + assignKeywords


    fun getWordType(word: String): TokenType? {
        return allKeywords[word]
    }
}
