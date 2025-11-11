sealed class Node {
    abstract override fun toString(): String

    data class Literal(val value: Any, val lineNumber: Int): Node() {
        override fun toString(): String = "$value"
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
