sealed class Node {
    abstract override fun toString(): String

    data class Literal(private val value: Any): Node() {
        override fun toString(): String = "$value"
    }
    data class Unary(private val operator: String, private val childNode: Node): Node() {
        override fun toString(): String = "($operator $childNode)"
    }
    data class Binary(private val operator: String, private val leftNode: Node, private val rightNode: Node): Node() {
        override fun toString(): String = "($leftNode $operator $rightNode)"
    }
    data class Group(private val childNode: Node): Node() {
        override fun toString(): String = "($childNode)"
    }
}