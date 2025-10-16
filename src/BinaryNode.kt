data class BinaryNode(
    val operator: String,
    val leftNode: ExpressionNode?,
    val rightNode: ExpressionNode?
) : ExpressionNode() {
    override fun getString(): String {
        return "(${operator} ${leftNode?.getString()} ${rightNode?.getString()})"
    }
}