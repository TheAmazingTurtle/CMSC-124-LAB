data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val lineNumber: Int
) {
    override fun toString(): String {
        return "Token(type=${type}, lexeme=${lexeme}, literal=${literal}, line=${lineNumber})"
    }
}