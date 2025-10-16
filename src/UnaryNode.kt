data class UnaryNode(
    val operator: String,
    val childNode: ExpressionNode?
) : ExpressionNode() {
    override fun getString(): String {
        return "(${operator} "+ childNode?.getString() +")"
    }
}