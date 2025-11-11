class Evaluator {
    private var activeLineNumber = -1
    private val errorMsg = mutableListOf<String>()

    fun getValueOfParseTree(parseTree: ParseTree): Any {
        errorMsg.clear()
        val rootNode = parseTree.rootNode
        return getValueOfNode(rootNode)
    }

    private fun getValueOfNode(node: Node): Any{
        return when (node) {
            is Node.Unary -> getValueOfUnaryNode(node)
            is Node.Binary -> getValueOfBinaryNode(node)
            is Node.Group -> getValueOfNode(node.childNode)
            is Node.Literal -> {
                activeLineNumber = node.lineNumber
                node.value
            }
        }
    }

    private fun getValueOfUnaryNode(unaryNode: Node.Unary) : Any {
        val nodeValue = getValueOfNode(unaryNode.childNode)

        when (unaryNode.operator){
            Operator.NOT -> {
                val boolValue = nodeValue as? Boolean ?: throw Exception(createErrorMsg("Invalid value type, expecting boolean"))
                return !boolValue
            }
            else -> throw Exception(createErrorMsg("Unrecognized unary operator"))
        }
    }

    private fun getValueOfBinaryNode(binaryNode: Node.Binary) : Any {
        val leftNodeValue = getValueOfNode(binaryNode.leftNode)
        val rightNodeValue = getValueOfNode(binaryNode.rightNode)

        return when (binaryNode.operator){
            in OperatorRegistry.logicalOperators -> executeLogicalOperator(binaryNode.operator, leftNodeValue, rightNodeValue)
            in OperatorRegistry.relationalOperators -> executeRelationalOperator(binaryNode.operator, leftNodeValue, rightNodeValue)
            in OperatorRegistry.arithmeticOperators ->  executeArithmeticOperator(binaryNode.operator, leftNodeValue, rightNodeValue)

            Operator.EQUAL -> leftNodeValue == rightNodeValue
            Operator.NOT_EQUAL -> leftNodeValue != rightNodeValue

            else -> throw Exception(createErrorMsg("Unrecognized binary operator"))
        }
    }

    private fun executeLogicalOperator(operator: Operator, leftNodeValue: Any, rightNodeValue: Any) : Boolean {
        val leftVal = leftNodeValue as? Boolean ?: throw Exception(createErrorMsg("Invalid value type, expecting boolean"))
        val rightVal = rightNodeValue as? Boolean ?: throw Exception(createErrorMsg("Invalid value type, expecting boolean"))

        return when (operator) {
            Operator.AND -> leftVal && rightVal
            Operator.OR -> leftVal || rightVal
            else -> throw Exception(createErrorMsg("Unrecognized logical operator"))
        }
    }

    private fun executeRelationalOperator(operator: Operator, leftNodeValue: Any, rightNodeValue: Any) : Boolean {
        val leftVal = (leftNodeValue as? Number)?.toDouble() ?: throw Exception(createErrorMsg("Invalid value type, expecting number"))
        val rightVal = (rightNodeValue as? Number)?.toDouble() ?: throw Exception(createErrorMsg("Invalid value type, expecting number"))

        return when (operator) {
            Operator.GREATER_EQUAL -> leftVal >= rightVal
            Operator.LESS_EQUAL -> leftVal <= rightVal
            Operator.GREATER -> leftVal > rightVal
            Operator.LESS -> leftVal < rightVal
            else -> throw Exception(createErrorMsg("Unrecognized relational operator"))
        }
    }

    private fun executeArithmeticOperator(operator: Operator, leftNodeValue: Any, rightNodeValue: Any) : Number {
        val isBothValueInt = leftNodeValue is Int && rightNodeValue is Int
        val leftVal = (leftNodeValue as? Number)?.toDouble() ?: throw Exception(createErrorMsg("Invalid value type, expecting number"))
        val rightVal = (rightNodeValue as? Number)?.toDouble() ?: throw Exception(createErrorMsg("Invalid value type, expecting number"))

        val result = when (operator) {
            Operator.ADD -> leftVal + rightVal
            Operator.SUBTRACT -> leftVal - rightVal
            Operator.MULTIPLY -> leftVal * rightVal
            Operator.DIVIDE ->
                if (rightVal == 0.0) throw Exception(createErrorMsg("Dividing by zero"))
                else leftVal / rightVal
            else -> throw Exception(createErrorMsg("Unrecognized arithmetic operator"))
        }

        return if (isBothValueInt) result.toInt() else result
    }

    private fun createErrorMsg(errorContent: String): String = "Runtime Error: $errorContent at line $activeLineNumber"
//
//    private fun raiseRuntimeError(errorMsg: String){
//        val completeErrorMsg = "Runtime Error: $errorMsg at line $activeLineNumber"
//        this.errorMsg.add(completeErrorMsg)
//        println(completeErrorMsg)
//    }
//
//    private fun failEvaluator(errorMsg: String): Nothing? {
//        raiseRuntimeError(errorMsg)
//        return null
//    }
//
//    fun isErrorFound(): Boolean = errorMsg.isNotEmpty()
//    fun getErrorMsgList(): List<String> = errorMsg.toList()
}