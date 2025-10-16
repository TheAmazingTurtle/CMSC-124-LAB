data class LiteralNode(
    val value: Any
) : ExpressionNode() {
    override fun getString(): String {
        return "${value}"
    }
}