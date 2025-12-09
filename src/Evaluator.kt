class Evaluator (private val environment: Environment){
    private var activeLineNumber = -1
    private val errorMsg = mutableListOf<String>()
    private var controlStructStack = mutableListOf<ControlStruct>()

    fun evaluateParseTree(parseTree: ParseTree) {
        errorMsg.clear()

        evaluateStatement(parseTree.rootNode)
    }

    private fun evaluateStatement(statement: Statement) {
        if (statement is Statement.Compound) {
            evaluateCompoundStatement(statement)
            return
        }

        evaluateSimpleStatement(statement)
    }

    private fun evaluateCompoundStatement(compoundStmt: Statement.Compound) {
        val ctrlStruct = when(compoundStmt) {
            is Statement.If -> ControlStruct.If(compoundStmt.condition)
            is Statement.Block -> ControlStruct.Block()
            is Statement.While -> ControlStruct.While(compoundStmt.condition)
        }

        if (controlStructStack.isNotEmpty()) {
            getInnermostCtrlStruct().recordExecutable(ctrlStruct)
        }

        controlStructStack.add(ctrlStruct)
    }

    private fun evaluateSimpleStatement(simpleStmt: Statement) {
        if (simpleStmt is Statement.End) {
            if (controlStructStack.isEmpty()) throw Exception(createErrorMsg("Unexpected End-Statement"))

            val expectedEndToken = when (getInnermostCtrlStruct()) {
                is ControlStruct.If -> TokenType.END_IF
                is ControlStruct.While -> TokenType.END_WHILE
                is ControlStruct.Block -> TokenType.END_BLOCK
            }

            if (simpleStmt.endType != expectedEndToken) {
                throw Exception(createErrorMsg("Expecting $expectedEndToken-Statement"))
            }

            val structuredStatement = popCtrlStruct()
            if (controlStructStack.isEmpty()) {
                executeControlStruct(structuredStatement)
            }

            return
        }

        if (controlStructStack.isNotEmpty()) {
            when (simpleStmt) {
                is Statement.Otherwise -> {
                    val ifStruct = getInnermostCtrlStruct()
                    if (ifStruct !is ControlStruct.If) throw Exception(createErrorMsg("Unexpected Otherwise-Statement"))

                    ifStruct.otherwiseEncountered = true
                    ifStruct.addBranch(simpleStmt.condition)
                }
                is Statement.OtherwiseIf -> {
                    val ifStruct = getInnermostCtrlStruct()
                    if (ifStruct !is ControlStruct.If) throw Exception(createErrorMsg("Unexpected Otherwise-Statement"))

                    ifStruct.addBranch(simpleStmt.condition)
                }
                else -> getInnermostCtrlStruct().recordExecutable(simpleStmt)
            }
            return
        }

        executeSimpleStatement(simpleStmt)
    }

    private fun performExecutables(executable: Executable){
        when (executable){
            is Statement -> executeSimpleStatement(executable)
            is ControlStruct -> executeControlStruct(executable)
        }
    }

    private fun executeControlStruct(ctrlStruct: ControlStruct) {
        environment.createInnerEnvironment()

        when (ctrlStruct){
            is ControlStruct.If -> {}
            is ControlStruct.While -> {}
            is ControlStruct.Block -> {
                for (executable in ctrlStruct.executables) {
                    performExecutables(executable)
                }
            }
        }

        environment.destroyInnerEnvironment()
    }

    private fun executeSimpleStatement(simpleStmt: Statement) {
        when(simpleStmt){
            is Statement.Set -> {
                val value = getValueOfNode(simpleStmt.value)
                environment.define(simpleStmt.name, value)
            }
            is Statement.Show -> {
                val value = getValueOfNode(simpleStmt.value)
                println(value)
            }
            else -> throw Exception(createErrorMsg("Unexpected statement"))
        }
    }


    private fun getValueOfNode(node: Node): Any{
        return when (node) {
            is Node.Currency -> node
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

                        processedParameterList.joinToString(separator = "")
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

    private fun getInnermostCtrlStruct(): ControlStruct = controlStructStack.last()
    private fun popCtrlStruct(): ControlStruct = controlStructStack.removeLast()
    private fun createErrorMsg(errorContent: String): String = "Runtime Error: $errorContent at line $activeLineNumber"

}