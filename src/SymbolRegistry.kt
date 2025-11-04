object SymbolRegistry {
    private val allSymbols = mapOf(
        "," to "COMMA"
    )

    fun getSymbolType(symbol: String): String? {
        return allSymbols[symbol]
    }
}