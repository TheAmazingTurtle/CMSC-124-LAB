class ParseTree {
    var rootNode: ExpressionNode? = null

    override fun toString(): String {
        return rootNode?.getString() ?:"MISSING ROOT NODE"
    }
}