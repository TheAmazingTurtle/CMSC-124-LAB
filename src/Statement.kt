sealed class Statement: Executable() {

    open fun isCompoundStatementInitial(): Boolean = false

    // Compound Statement
    sealed class Compound : Statement() {
        override fun isCompoundStatementInitial() = true
    }

    data class Block(val unit: Unit = Unit) : Compound()
    data class While(val condition: Node) : Compound()
    data class If(val condition: Node) : Compound()

    // Branch Extension
    data class OtherwiseIf(val condition: Node) : Statement()
    data class Otherwise(val condition: Node = Node.Literal(true, -1)) : Statement()

    // Simple Statement
    data class Set(val name: String, val value: Node) : Statement()
    data class Show(val value: Node) : Statement()
    data class End(val endType: TokenType) : Statement()
}
