import kotlin.collections.listOf

class Parser {
    private var tokenList = listOf<Token>()
    private var index = 0
    private val errorMsg = mutableListOf<String>()

    fun getParseTree(tokenList: List<Token>): ParseTree {
        setupParser(tokenList)
        val parseTree = ParseTree(parseExpression())

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

            val rightNode = andOperation() ?: return null
            leftNode = Node.Binary(Operator.OR, leftNode, rightNode)
        }

        return leftNode
    }

    private fun andOperation(): Node? {
        var leftNode = equalityOperation() ?: return null

        while (getCurToken().type == TokenType.AND){
            consumeToken()

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
            val rightNode = unaryOperation() ?: return null

            leftNode = Node.Binary(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun unaryOperation(): Node? {
        return if (getCurToken().type == TokenType.NOT){
            val node = unaryOperation() ?: return null
            Node.Unary(Operator.NOT, node)
        } else {
            literalValue()
        }
    }

    private fun literalValue(): Node? {
        val literalNode = when (getCurToken().type) {
            in setOf(TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN) -> {
                val literal = getCurToken().literal
                Node.Literal(literal, getCurToken().lineNumber)
            }
            TokenType.IDENTIFIER -> Node.Literal(getCurToken().lexeme, getCurToken().lineNumber)
            TokenType.EOL -> {
                raiseParseError("Improper code sequence")
                null
            }
            else -> {
                raiseParseError("Unexpected token")
                null
            }
        }

        consumeToken()
        return literalNode
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

    private fun raiseParseError(errorMsg: String) {
        this.errorMsg.add("Parsing Error: $errorMsg at line ${getCurToken().lineNumber}")
    }

    private fun getCurToken(): Token = tokenList[index]
    private fun consumeToken(numOfTokensToConsume: Int = 1) {
        index += numOfTokensToConsume
    }
}