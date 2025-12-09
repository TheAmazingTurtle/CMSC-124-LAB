object OperatorRegistry {
    val logicalOperators = setOf(Operator.AND, Operator.OR, Operator.NOT)
    val relationalOperators = setOf(Operator.GREATER_EQUAL, Operator.GREATER, Operator.LESS_EQUAL, Operator.LESS)
    val arithmeticOperators = setOf(Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE)
    val equalityOperators = setOf(Operator.EQUAL, Operator.NOT_EQUAL)
}