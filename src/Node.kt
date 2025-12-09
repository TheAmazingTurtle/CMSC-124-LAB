sealed class Node {
    abstract override fun toString(): String

    data class Currency(val value: Double, val code: TokenType, val lineNumber: Int): Node() {
        override fun toString(): String = "$value$code"
    }

    data class Literal(val value: Any, val lineNumber: Int): Node() {
        override fun toString(): String = "$value"
    }

    data class Variable(val name: String, val lineNumber: Int): Node() {
        override fun toString(): String = name
    }

    data class Function(val name: TokenType, val parameter: List<Node>, val lineNumber: Int): Node() {
        override fun toString(): String = "$name(${parameter.joinToString()})"
    }

    data class Unary(val operator: Operator, val childNode: Node): Node() {
        override fun toString(): String = "($operator $childNode)"
    }
    data class Binary(val operator: Operator, val leftNode: Node, val rightNode: Node): Node() {
        override fun toString(): String = "($leftNode $operator $rightNode)"
    }
    data class Group(val childNode: Node): Node() {
        override fun toString(): String = "($childNode)"
    }
}
