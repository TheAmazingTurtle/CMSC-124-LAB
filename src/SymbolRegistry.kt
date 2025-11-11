object SymbolRegistry {
    private val allSymbols = mapOf(
        "," to TokenType.COMMA,
        "(" to TokenType.OPEN_PARENTHESIS,
        ")" to TokenType.CLOSE_PARENTHESIS
    )

    fun getSymbolType(symbol: String): TokenType? {
        return allSymbols[symbol]
    }
}