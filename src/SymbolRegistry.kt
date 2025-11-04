object SymbolRegistry {
    private val allSymbols = mapOf(
        "," to TokenType.COMMA
    )

    fun getSymbolType(symbol: String): TokenType? {
        return allSymbols[symbol]
    }
}