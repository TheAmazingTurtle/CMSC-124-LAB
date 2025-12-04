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
        "as" to TokenType.AS,
        "this" to TokenType.THIS
    )

    private val printKeyword = mapOf(
        "show" to TokenType.SHOW
    )

    private val exitKeyword = mapOf(
        "quit" to TokenType.QUIT
    )

    private val blockKeywords = mapOf(
        "block" to TokenType.BLOCK,
        "end_block" to TokenType.END_BLOCK
    )

    private val conditionalKeywords = mapOf(
        "if" to TokenType.IF,
        "then" to TokenType.THEN,
        "otherwise" to TokenType.OTHERWISE,
        "end_if" to TokenType.END_IF,
    )

    private val loopKeywords = mapOf(
        "while" to TokenType.WHILE,
        "do" to TokenType.DO,
        "for" to TokenType.FOR,
        "end_while" to TokenType.END_WHILE,
        "end_for" to TokenType.END_FOR
    )

    private val switchKeywords = mapOf(
        "based" to TokenType.BASED,
        "when" to TokenType.WHEN,
        "escape" to TokenType.ESCAPE,
        "end_based" to TokenType.END_BASED
    )

    private val builtInFunctionNameKeywords = mapOf(
        "concat_to_string" to TokenType.CONCAT
    )

    private val functionKeywords = mapOf(
        "using" to TokenType.USING,
        "only" to TokenType.ONLY
    )

    private val endKeywords = setOf(
        TokenType.END_IF, TokenType.END_FOR, TokenType.END_WHILE, TokenType.END_BASED
    )

    private val allKeywords = arithmeticKeywords + logicKeywords + assignKeywords + printKeyword + conditionalKeywords + loopKeywords + switchKeywords+ exitKeyword + blockKeywords + builtInFunctionNameKeywords + functionKeywords
    private val statementKeywords = assignKeywords + printKeyword + conditionalKeywords + loopKeywords + switchKeywords+ exitKeyword + blockKeywords

    fun getWordType(word: String): TokenType? = allKeywords[word]
    fun getFunctionKeyword(): Collection<TokenType> = builtInFunctionNameKeywords.values
    fun isStatementKeyword(type: TokenType): Boolean = type in statementKeywords.values
    fun getEndKeywords(): Set<TokenType> = endKeywords
}
