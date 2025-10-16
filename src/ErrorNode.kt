class ErrorNode (
    val errorMsg: String,
    val lineNum: Int,
    val location: String
): ExpressionNode() {
    override fun getString(): String {
        return "[Line $lineNum] Error at $location: $errorMsg"
    }
}