object KeywordRegistry {
    private val arithmeticKeywords = mapOf(
        "added" to "ADDED",
        "subtracted" to "SUBTRACTED",
        "multiplied" to "MULTIPLIED",
        "divided" to "DIVIDED",
        "by" to "BY"
    )

    private val logicKeywords = mapOf(
        "add" to "ADD",
        "or" to "OR",
        "not" to "NOT",
        "is" to "IS",
        "equal" to "EQUAL",
        "greater" to "GREATER",
        "less" to "LESS",
        "than" to "THAN",
        "true" to "TRUE",
        "false" to "FALSE"
    )

    private val assignKeywords = mapOf(
        "set" to "SET",
        "to" to "TO",
        "as" to "AS"
    )

    private val allKeywords = arithmeticKeywords + logicKeywords + assignKeywords


    fun getWordType(word: String): String? {
        return allKeywords[word]
    }
}