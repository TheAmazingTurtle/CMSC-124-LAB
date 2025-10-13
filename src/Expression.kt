sealed class ExpressionNode
data class LiteralNode(val parentNode: ExpressionNode?, val value: Any) : ExpressionNode()
data class UnaryNode(val parentNode: ExpressionNode?, val operator: String, val childNode: ExpressionNode?) : ExpressionNode()
data class BinaryNode(val parentNode: ExpressionNode?, val operator: String, val leftNode: ExpressionNode?, val rightNode: ExpressionNode?) : ExpressionNode()
data class GroupNode(val parentNode: ExpressionNode?, val childNode: ExpressionNode?) : ExpressionNode()