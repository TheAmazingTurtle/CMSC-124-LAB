class Evaluator {
    var activeLineNumber = -1
    val errorMsg = mutableListOf<String>()

    fun getValueOfParseTree(parseTree: ParseTree): Any? {
        val rootNode = parseTree.rootNode ?: return null

        return getValueOfNode(rootNode)
    }

    private fun getValueOfNode(node: Node): Any?{
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

    private fun getValueOfUnaryNode(unaryNode: Node.Unary) : Any? {
        val nodeValue = getValueOfNode(unaryNode.childNode) ?: return null

        when (unaryNode.operator){
            Operator.NOT -> {
                val boolValue = nodeValue as? Boolean ?: return failRuntime("Invalid value type, expecting boolean")
                return !boolValue
            }
            else -> return failRuntime("Unrecognized unary operator")
        }
    }

    private fun getValueOfBinaryNode(binaryNode: Node.Binary) : Any? {
        val leftNodeValue = getValueOfNode(binaryNode.leftNode) ?: return null
        val rightNodeValue = getValueOfNode(binaryNode.rightNode) ?: return null

        return when (binaryNode.operator){
            in OperatorRegistry.logicalOperators -> executeLogicalOperator(binaryNode.operator, leftNodeValue, rightNodeValue)
            in OperatorRegistry.relationalOperators -> executeRelationalOperator(binaryNode.operator, leftNodeValue, rightNodeValue)
            in OperatorRegistry.arithmeticOperators ->  executeArithmeticOperator(binaryNode.operator, leftNodeValue, rightNodeValue)

            Operator.EQUAL -> leftNodeValue == rightNodeValue
            Operator.NOT_EQUAL -> leftNodeValue != rightNodeValue

            else -> failRuntime("Unrecognized binary operator")
        }
    }

    private fun executeLogicalOperator(operator: Operator, leftNodeValue: Any, rightNodeValue: Any) : Boolean? {
        val leftVal = leftNodeValue as? Boolean ?: return failRuntime("Invalid value type, expecting boolean")
        val rightVal = rightNodeValue as? Boolean ?: return failRuntime("Invalid value type, expecting boolean")

        return when (operator) {
            Operator.AND -> leftVal && rightVal
            Operator.OR -> leftVal || rightVal
            else -> return failRuntime("Unrecognized logical operator")
        }
    }

    private fun executeRelationalOperator(operator: Operator, leftNodeValue: Any, rightNodeValue: Any) : Boolean? {
        val leftVal = (leftNodeValue as? Number)?.toDouble() ?: return failRuntime("Invalid value type, expecting number")
        val rightVal = (rightNodeValue as? Number)?.toDouble() ?: return failRuntime("Invalid value type, expecting number")

        return when (operator) {
            Operator.GREATER_EQUAL -> leftVal >= rightVal
            Operator.LESS_EQUAL -> leftVal <= rightVal
            Operator.GREATER -> leftVal > rightVal
            Operator.LESS -> leftVal < rightVal
            else -> return failRuntime("Unrecognized relational operator")
        }
    }

    private fun executeArithmeticOperator(operator: Operator, leftNodeValue: Any, rightNodeValue: Any) : Number? {
        val isBothValueInt = leftNodeValue is Int && rightNodeValue is Int
        val leftVal = (leftNodeValue as? Number)?.toDouble() ?: return raiseRuntimeError("Invalid value type, expecting number").let { null }
        val rightVal = (rightNodeValue as? Number)?.toDouble() ?: return raiseRuntimeError("Invalid value type, expecting number").let { null }

        val result = when (operator) {
            Operator.ADD -> leftVal + rightVal
            Operator.SUBTRACT -> leftVal - rightVal
            Operator.MULTIPLY -> leftVal * rightVal
            Operator.DIVIDE ->
                if (rightVal == 0.0) return failRuntime("Dividing by zero")
                else leftVal / rightVal
            else -> return failRuntime("Unrecognized arithmetic operator")
        }

        return if (isBothValueInt) result.toInt() else result
    }

    private fun raiseRuntimeError(errorMsg: String){
        this.errorMsg.add("Runtime Error: $errorMsg at line $activeLineNumber")
    }

    private fun failRuntime(errorMsg: String): Nothing? {
        raiseRuntimeError(errorMsg)
        return null
    }
}