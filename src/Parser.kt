class Parser() {

    private var index = 0
    private var tokenList = listOf<Token>()
    private var placeholderToken = Token("PLACEHOLDER", "NULL", null, 0)
    private var curToken = placeholderToken
    private var nextToken = placeholderToken

    private fun initParser(){
        index = 0
        curToken = tokenList[index]
        nextToken = tokenList.getOrNull(index+1) ?: placeholderToken
    }

    fun parseLine(tokenList: List<Token>): ParseTree{
        val exprParseTree = ParseTree()

        this.tokenList = tokenList

        initParser()
        exprParseTree.rootNode = evaluateExpression()

        if(index < tokenList.size - 1){
            exprParseTree.rootNode = ErrorNode("Improper expression sequence", curToken.lineNumber, curToken.lexeme)
        }

        return exprParseTree
    }

    private fun evaluateExpression(): ExpressionNode{
        return orOperation()
    }

    private fun orOperation(): ExpressionNode{

        val leftNode = andOperation()

        if (leftNode is ErrorNode)
            return leftNode

        if (curToken.type == "OR"){
            val operator = curToken.lexeme
            lex()

            val rightNode = orOperation()
            if (rightNode is ErrorNode)
                return rightNode

            return BinaryNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun andOperation(): ExpressionNode {
        val leftNode = equalityOperation()

        if (leftNode is ErrorNode)
            return leftNode

        if (curToken.type == "AND"){
            val operator = curToken.lexeme
            lex()

            val rightNode = andOperation()
            if (rightNode is ErrorNode)
                return rightNode

            return BinaryNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun equalityOperation(): ExpressionNode {
        val leftNode = relationalOperation()

        if (leftNode is ErrorNode)
            return leftNode

        if (curToken.type in setOf("EQUAL", "NOT_EQUAL")){
            val operator = curToken.lexeme
            lex()

            val rightNode = equalityOperation()
            if (rightNode is ErrorNode)
                return rightNode

            return BinaryNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun relationalOperation(): ExpressionNode {
        val leftNode = term()

        if (leftNode is ErrorNode)
            return leftNode

        if (curToken.type in setOf("LESSER", "GREATER", "LESSER_EQUAL", "GREATER_EQUAL", "NOT_EQUAL")){
            val operator = curToken.lexeme
            lex()

            val rightNode = relationalOperation()
            if (rightNode is ErrorNode)
                return rightNode

            return BinaryNode(operator, leftNode, rightNode)
        }

        return leftNode
    }
    private fun term():  ExpressionNode{
        val leftNode = factor()

        if (leftNode is ErrorNode)
            return leftNode

        if (curToken.type in setOf("PLUS", "MINUS")){
            val operator = curToken.lexeme
            lex()

            val rightNode = term()
            if (rightNode is ErrorNode)
                return rightNode

            return BinaryNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun factor():  ExpressionNode{
        val leftNode = unaryOperation()

        if (leftNode is ErrorNode) return leftNode

        if (curToken.type in setOf("MULTIPLY", "DIVIDE", "MODULO")){
            val operator = curToken.lexeme
            lex()

            val rightNode = factor()
            if (rightNode is ErrorNode )
                return rightNode

            return BinaryNode(operator, leftNode, rightNode)
        }

        return leftNode
    }


    private fun unaryOperation(): ExpressionNode {
        if (curToken.type in setOf("PLUS","MINUS","NOT","INCREMENT","DECREMENT")){
            val operator = curToken.lexeme
            lex()

            val node = unaryOperation()
            if (node is ErrorNode)
                return node

            return UnaryNode(operator, node)
        }
        else {
            return literal()
        }

    }

    private fun literal(): ExpressionNode {
        val node =  when (curToken.type) {
                        in setOf("INT_NUMBER", "FLOAT_NUMBER", "CHAR", "STRING", "TRUE", "FALSE") -> LiteralNode(curToken.lexeme)
                        "IDENTIFIER" -> {
                            if (nextToken.type in setOf("INCREMENT", "DECREMENT")) {
                                val unaryOperator = nextToken.lexeme
                                val childNode = LiteralNode(curToken.literal ?: curToken.lexeme)
                                lex()

                                UnaryNode(unaryOperator, childNode)
                            } else {
                                LiteralNode(curToken.lexeme)
                            }
                        }
                        "L_PAREN"   ->  {
                            lex()
                            val innerExpr = evaluateExpression()

                            if (innerExpr is ErrorNode){
                                return innerExpr
                            }

                            if (curToken.type != "R_PAREN") {
                                ErrorNode("Expect ')' after expression", curToken.lineNumber, "end of {${innerExpr.getString()}}")
                            }
                            else GroupNode(innerExpr)
                        }
                        "EOF" -> ErrorNode("Improper expression sequence", curToken.lineNumber, curToken.lexeme)
                        else -> ErrorNode("Unexpected token", curToken.lineNumber, curToken.lexeme)
                    }
        lex()
        return node
    }

    private fun lex(){
        if (curToken.type == "EOF"){
            return
        }

        index++
        curToken = nextToken
        nextToken = tokenList.getOrNull(index+1) ?: placeholderToken
    }

}

