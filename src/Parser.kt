import kotlin.collections.listOf

class Parser {
    private var tokenList = listOf<Token>()
    private var index = 0
    private val errorMsg = mutableListOf<String>()

    fun getParseTree(tokenList: List<Token>): ParseTree? {
        setupParser(tokenList)

        if (tokenList.size <= 1) return failParser("Empty expression")

        val rootNode = parseExpression()  ?: return null
        val parseTree = ParseTree(rootNode)

        if (getCurToken().type != TokenType.EOL){
            raiseParseError("Improper coding sequence")
        }

        return parseTree
    }
    fun isErrorFound(): Boolean = errorMsg.isNotEmpty()
    fun getErrorMsgList(): List<String> = errorMsg.toList()

    private fun parseExpression(): Node? = orOperation()
    private fun orOperation(): Node? {
        var leftNode = andOperation() ?: return null

        while (getCurToken().type == TokenType.OR){
            consumeToken()
            if (!hasMoreTokens()) return failParser("Missing operand after ${TokenType.OR} operator")

            val rightNode = andOperation() ?: return null
            leftNode = Node.Binary(Operator.OR, leftNode, rightNode)
        }

        return leftNode
    }

    private fun andOperation(): Node? {
        var leftNode = equalityOperation() ?: return null

        while (getCurToken().type == TokenType.AND){
            consumeToken()
            if (!hasMoreTokens()) return failParser("Missing operand after ${TokenType.AND} operator")

            val rightNode = equalityOperation() ?: return null
            leftNode = Node.Binary(Operator.AND, leftNode, rightNode)
        }

        return leftNode
    }

    private fun equalityOperation(): Node? {
        var leftNode = relationalOperator() ?: return null

        while (true){
            val operator = when {
                checkTokenTypeSequence(TokenType.IS, TokenType.EQUAL, TokenType.TO) ->
                    Operator.EQUAL
                checkTokenTypeSequence(TokenType.IS, TokenType.NOT, TokenType.EQUAL, TokenType.TO) ->
                    Operator.NOT_EQUAL
                else -> break
            }
            if (!hasMoreTokens()) return failParser("Missing operand after $operator operator")
            val rightNode = relationalOperator() ?: return null

            leftNode = Node.Binary(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun relationalOperator(): Node? {
        var leftNode = arithmeticOperation() ?: return null

        while (true){
            val operator = when {
                checkTokenTypeSequence(TokenType.IS, TokenType.GREATER, TokenType.THAN, TokenType.OR, TokenType.EQUAL, TokenType.TO) ->
                    Operator.GREATER_EQUAL
                checkTokenTypeSequence(TokenType.IS, TokenType.LESS, TokenType.THAN, TokenType.OR, TokenType.EQUAL, TokenType.TO) ->
                    Operator.LESS_EQUAL
                checkTokenTypeSequence(TokenType.IS, TokenType.GREATER, TokenType.THAN) ->
                    Operator.GREATER
                checkTokenTypeSequence(TokenType.IS, TokenType.LESS, TokenType.THAN) ->
                    Operator.LESS
                else -> break
            }
            if (!hasMoreTokens()) return failParser("Missing operand after $operator operator")
            val rightNode = arithmeticOperation() ?: return null

            leftNode = Node.Binary(operator, leftNode, rightNode)

        }

        return leftNode
    }

    private fun arithmeticOperation(): Node? {
        var leftNode = unaryOperation() ?: return null

        while (true) {
            val operator = when {
                checkTokenTypeSequence(TokenType.ADDED, TokenType.BY) -> Operator.ADD
                checkTokenTypeSequence(TokenType.SUBTRACTED, TokenType.BY) -> Operator.SUBTRACT
                checkTokenTypeSequence(TokenType.MULTIPLIED, TokenType.BY) -> Operator.MULTIPLY
                checkTokenTypeSequence(TokenType.DIVIDED, TokenType.BY) -> Operator.DIVIDE
                else -> break
            }
            if (!hasMoreTokens()) return failParser("Missing operand after $operator operator")
            val rightNode = unaryOperation() ?: return null

            leftNode = Node.Binary(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun unaryOperation(): Node? {
        return if (getCurToken().type == TokenType.NOT){
            consumeToken()
            if (!hasMoreTokens()) return failParser("Missing operand after ${TokenType.NOT} operator")

            val node = unaryOperation() ?: return null
            Node.Unary(Operator.NOT, node)
        } else {
            literalValue()
        }
    }

    private fun literalValue(): Node? {
        val node = when (getCurToken().type) {
            in setOf(TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN) -> {
                val literal = getCurToken().literal
                Node.Literal(literal, getCurToken().lineNumber)
            }
            TokenType.IDENTIFIER -> Node.Literal(getCurToken().lexeme, getCurToken().lineNumber)
            TokenType.OPEN_PARENTHESIS -> {
                consumeToken()
                if (!hasMoreTokens()) return failParser("Missing expression after ${TokenType.OPEN_PARENTHESIS} symbol")
                val innerNode = parseExpression() ?: return null

                if (getCurToken().type != TokenType.CLOSE_PARENTHESIS) failParser("Expect ')' after expression")
                else Node.Group(innerNode)
            }
            TokenType.EOL -> {
                return failParser("Improper code sequence")
            }
            else -> {
                return failParser("Unexpected token")
            }
        }

        consumeToken()
        return node
    }

    private fun setupParser(tokenList: List<Token>) {
        this.tokenList = tokenList
        this.errorMsg.clear()
        index = 0
    }

    private fun checkTokenTypeSequence(vararg typeSequence: TokenType): Boolean {
        if (index + typeSequence.size >= tokenList.size) {
            return false
        }

        for (i in typeSequence.indices){
            if (tokenList[index + i].type != typeSequence[i]){
                return false
            }
        }

        consumeToken(typeSequence.size)
        return true
    }



    private fun raiseParseError(errorMsg: String) = {
        val completeErrorMsg = "Parsing Error: $errorMsg at line ${getCurToken().lineNumber}"
        this.errorMsg.add(completeErrorMsg)
        println(completeErrorMsg)
    }
    private fun failParser(errorMsg: String): Nothing? = raiseParseError(errorMsg).let { null }
    private fun hasMoreTokens(): Boolean = index < tokenList.size;
    private fun getCurToken(): Token = tokenList[index]
    private fun consumeToken(numOfTokensToConsume: Int = 1) {
        index += numOfTokensToConsume
    }
}