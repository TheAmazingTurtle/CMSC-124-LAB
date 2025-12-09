object CurrencyRate {
// 12-10-2025, 2:50am phil
    private val conversionRateToPhp = mapOf(
        TokenType.PHP to 1.0,
        TokenType.USD to 59.32,
        TokenType.EUR to 69.84,
        TokenType.GBP to 78.88,
        TokenType.KRW to 0.040,
        TokenType.JPY to 0.38,
    )

    fun getCurrencyMultiplier(type: TokenType): Double? = conversionRateToPhp[type]
}



