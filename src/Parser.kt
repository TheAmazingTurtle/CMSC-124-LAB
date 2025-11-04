import kotlin.collections.listOf

class Parser {
    private var tokenList = listOf<Token?>()
    private var index = 0
    private val errorMsg = mutableListOf<String>()

    fun getParseTree(tokenList: List<Token?>): ParseTree {
        setupParser(tokenList)

        return ParseTree(parseExpression())
    }
    fun isErrorFound(): Boolean = errorMsg.isNotEmpty()
    fun getErrorMsgList(): List<String> = errorMsg.toList()

    private fun parseExpression(): Node = orOperation()
    private fun orOperation(): Node {
        var leftNode = andOperation()

        while (getCurToken().type == TokenType.OR){
            val operator = getCurToken().lexeme
            consumeToken()
            leftNode = Node.Binary(operator, leftNode, andOperation())
        }

        return leftNode
    }

    private fun andOperation(): Node {
        var leftNode = equalityOperation()

        while (getCurToken().type == TokenType.AND){
            val operator = getCurToken().lexeme
            consumeToken()
            leftNode = Node.Binary(operator, leftNode, equalityOperation())
        }

        return leftNode
    }

    private fun equalityOperation(): Node {
        var leftNode = relationalOperator()

        while (true){
            leftNode = when {
                checkTokenTypeSequence(TokenType.IS, TokenType.EQUAL, TokenType.TO) ->
                    Node.Binary("is equal to", leftNode, relationalOperator())

                checkTokenTypeSequence(TokenType.IS, TokenType.NOT, TokenType.EQUAL, TokenType.TO) ->
                    Node.Binary("is not equal to", leftNode, relationalOperator())

                else -> break
            }
        }

        return leftNode
    }

    private fun relationalOperator(): Node {
        var leftNode = arithmeticOperation()

        while (true){
            leftNode = when {
                checkTokenTypeSequence(TokenType.IS, TokenType.GREATER, TokenType.THAN, TokenType.OR, TokenType.EQUAL, TokenType.TO) ->
                    Node.Binary("is greater than or equal to", leftNode, arithmeticOperation())

                checkTokenTypeSequence(TokenType.IS, TokenType.LESS, TokenType.THAN, TokenType.OR, TokenType.EQUAL, TokenType.TO) ->
                    Node.Binary("is less than or equal to", leftNode, arithmeticOperation())

                checkTokenTypeSequence(TokenType.IS, TokenType.GREATER, TokenType.THAN) ->
                    Node.Binary("is greater than", leftNode, arithmeticOperation())

                checkTokenTypeSequence(TokenType.IS, TokenType.LESS, TokenType.THAN) ->
                    Node.Binary("is less than", leftNode, arithmeticOperation())

                else -> break
            }
        }

        return leftNode
    }

    private fun arithmeticOperation(): Node{
        var leftNode = unaryOperation()

        while (true) {
            leftNode = when {
                checkTokenTypeSequence(TokenType.ADDED, TokenType.BY) ->
                    Node.Binary("added by", leftNode, unaryOperation())
                checkTokenTypeSequence(TokenType.SUBTRACTED, TokenType.BY) ->
                    Node.Binary("subtracted by", leftNode, unaryOperation())
                checkTokenTypeSequence(TokenType.MULTIPLIED, TokenType.BY) ->
                    Node.Binary("multiplied by", leftNode, unaryOperation())
                checkTokenTypeSequence(TokenType.DIVIDED, TokenType.BY) ->
                    Node.Binary("divided by", leftNode, unaryOperation())
                else -> break
            }
        }

        return leftNode
    }

    private fun unaryOperation(): Node {
        return if (getCurToken().type == TokenType.NOT){
            Node.Unary("not", unaryOperation())
        } else {
            literalValue()
        }
    }

    private fun literalValue(): Node {
        val literalNode = when (getCurToken().type) {
            in setOf(TokenType.STRING, TokenType.NUMBER, TokenType.TRUE, TokenType.FALSE) -> Node.Literal(getCurToken().literal?:"")
            TokenType.IDENTIFIER -> Node.Literal(getCurToken().lexeme)
            TokenType.EOL -> {
                raiseParseError("Improper code sequence")
                Node.Literal("null")
            }
            else -> {
                raiseParseError("Unexpected token")
                Node.Literal("null")
            }
        }

        consumeToken()
        return literalNode
    }

    private fun setupParser(tokenList: List<Token?>) {
        this.tokenList = tokenList
        this.errorMsg.clear()
        index = 0
    }

    private fun checkTokenTypeSequence(vararg typeSequence: TokenType): Boolean {
        if (index + typeSequence.size >= tokenList.size) {
            return false
        }

        for (i in typeSequence.indices){
            if (tokenList[index + i]?.type != typeSequence[i]){
                return false
            }
        }

        consumeToken(typeSequence.size)
        return true
    }

    private fun raiseParseError(errorMsg: String) {
        this.errorMsg.add("Error: $errorMsg at line ${getCurToken().lineNumber}")
    }

    private fun getCurToken(): Token = tokenList[index] ?: Token(TokenType.INVALID, "NULL", null, -1)
    private fun consumeToken(numOfTokensToConsume: Int = 1) {
        index += numOfTokensToConsume
    }
}