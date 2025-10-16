data class GroupNode(
    val childNode: ExpressionNode?
) : ExpressionNode() {
    override fun getString(): String {
        return "("+ childNode?.getString() +")"
    }
}