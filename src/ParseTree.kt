class ParseTree(
    val rootNode: Statement
) {
    override fun toString(): String {
        return "$rootNode"
    }
}