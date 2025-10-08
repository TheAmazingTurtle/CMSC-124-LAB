sealed class Expression
data class Literal(val parentExpr: Expression, val value: Any) : Expression()
data class Unary(val parentExpr: Expression, val operator: String, val childExpr: Expression) : Expression()
data class Binary(val parentExpr: Expression, val operator: String, val leftExpr: Expression, val rightExpr: Expression) : Expression()
data class Group(val parentExpr: Expression, val childExpr: Expression) : Expression()