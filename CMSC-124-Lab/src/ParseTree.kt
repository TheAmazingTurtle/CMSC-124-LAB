class ParsingTree {
    var rootNode: ExpressionNode? = null

    override fun toString(): String {
        return rootNode?.printNode() ?:"MISSING ROOT NODE"
    }
}