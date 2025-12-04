class Evaluator (private val environment: Environment){
    private var activeLineNumber = -1
    private val errorMsg = mutableListOf<String>()
    private var activeControlStructStack = mutableListOf<ControlStruct>()

    fun evaluateParseTree(parseTree: ParseTree) {
        errorMsg.clear()
        val rootNode = parseTree.rootNode
        if (rootNode is Statement)
            executeStatement(rootNode)
        else
            throw Exception(createErrorMsg("Expecting statement"))

    }

//    fun getValueOfParseTree(parseTree: ParseTree): Any {
//        errorMsg.clear()
//        return when(val rootNode = parseTree.rootNode){
//            is Node -> getValueOfNode((rootNode))
//            is Statement -> {
//                executeStatement(rootNode)
//            }
//            else -> throw Exception(createErrorMsg("Unknown statement and expression type"))
//        }
//    }

    private fun getValueOfNode(node: Node): Any{
        return when (node) {
            is Node.Unary -> getValueOfUnaryNode(node)
            is Node.Binary -> getValueOfBinaryNode(node)
            is Node.Group -> getValueOfNode(node.childNode)
            is Node.Function -> {
                activeLineNumber = node.lineNumber
                when (node.name) {
                    TokenType.CONCAT -> {
                        val processedParameterList = mutableListOf<Any>()
                        for (parameter in node.parameter){
                            processedParameterList.add(getValueOfNode(parameter))
                        }

                        processedParameterList.joinToString(separator = "");
                    }
                    else -> throw Exception(createErrorMsg("Unrecognized function"))
                }

            }
            is Node.Variable -> {
                activeLineNumber = node.lineNumber
                environment.getValue(node.name) ?: throw Exception(createErrorMsg("Undefined variable ${node.name}"))
            }
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

    private fun executeStatement(statement: Statement) {
        if (activeControlStructStack.isNotEmpty()) {
            when(activeControlStructStack.last()) {
                is ControlStruct.If -> {
                    val currentIfStruct = activeControlStructStack.last() as ControlStruct.If

                    when(statement) {
                        is Statement.OtherwiseIf -> {
                            if (currentIfStruct.otherwiseSeen) throw Exception(createErrorMsg("Improper if structure"))
                            if (currentIfStruct.conditionSatisfied) {
                                if (!currentIfStruct.trueBlockExecuted) currentIfStruct.trueBlockExecuted = true
                                return
                            }

                            val value = getValueOfNode(statement.expression)
                            if (value !is Boolean) throw Exception(createErrorMsg("Expecting boolean result from expression"))
                            if (value) currentIfStruct.conditionSatisfied = true
                            return
                        }
                        is Statement.Otherwise ->  {
                            if (currentIfStruct.otherwiseSeen) throw Exception(createErrorMsg("Improper if structure"))
                            currentIfStruct.otherwiseSeen = true
                            if (currentIfStruct.conditionSatisfied) {
                                if (!currentIfStruct.trueBlockExecuted) currentIfStruct.trueBlockExecuted = true
                                return
                            }

                            currentIfStruct.conditionSatisfied = true
                            return
                        }
                        is Statement.EndIf -> {
                            environment.destroyInnerEnvironment()
                            activeControlStructStack.removeLast()
                            return
                        }
                        else -> {
                            if(!currentIfStruct.conditionSatisfied || currentIfStruct.trueBlockExecuted) return
                        }
                    }
                }

                is ControlStruct.While -> {
                    val currentWhileStruct = activeControlStructStack.last() as ControlStruct.While

                    if (statement is Statement.EndWhile) {
                        currentWhileStruct.recordingDone = true

                        val recorded = ArrayList(currentWhileStruct.statementRecord)
                        var condValue = getValueOfNode(currentWhileStruct.booleanCondition)
                        if (condValue !is Boolean) {
                            throw Exception(createErrorMsg("Expecting boolean result from while condition"))
                        }
                        while ((condValue as? Boolean) == true) {
                            for (statement in recorded){
                                executeStatement(statement)
                            }

                            condValue = getValueOfNode(currentWhileStruct.booleanCondition)
                            if (condValue !is Boolean) {
                                throw Exception(createErrorMsg("Expecting boolean result from while condition"))
                            }
                        }
                    } else {
                        currentWhileStruct.statementRecord.add(statement)
                    }
                    return

                }
            }

        }

        when(statement){
            is Statement.SetVariable -> {
                val value = getValueOfNode(statement.value)
                environment.define(statement.name, value)
            }
            is Statement.Show -> {
                val value = getValueOfNode(statement.value)
                println(value)
            }
            is Statement.Block -> {
                if (statement.enterBlock)
                    environment.createInnerEnvironment()
                else
                    environment.destroyInnerEnvironment()
            }
            is Statement.If -> {
                activeControlStructStack.add(ControlStruct.If())
                environment.createInnerEnvironment()

                val value = getValueOfNode(statement.expression)
                if (value !is Boolean) throw Exception(createErrorMsg("Expecting boolean result from expression"))

                val currentIfStruct = activeControlStructStack.last() as ControlStruct.If
                if (value) currentIfStruct.conditionSatisfied = true
            }

            is Statement.While -> {
                activeControlStructStack.add(ControlStruct.While(statement.expression))
                environment.createInnerEnvironment()
            }

            is Statement.OtherwiseIf -> throw Exception(createErrorMsg("Missing initial if-statement"))
            is Statement.Otherwise -> throw Exception(createErrorMsg("Missing initial if-statement"))
            is Statement.EndIf -> throw Exception(createErrorMsg("Missing initial if-statement"))
            is Statement.EndWhile -> throw Exception(createErrorMsg("Missing initial while-statement"))

            else -> throw Exception(createErrorMsg("Unexpected statement"))
        }
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