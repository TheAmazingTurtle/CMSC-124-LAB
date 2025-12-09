import kotlin.collections.listOf
//if else
// while
// for
// functions

class Parser {
    private var tokenList = listOf<Token>()
    private var index = 0
    private val errorMsg = mutableListOf<String>()

    fun getParseTree(tokenList: List<Token>): ParseTree {
        setupParser(tokenList)

        if (getCurToken().type == TokenType.EOL) throw Exception(createErrorMsg("Empty expression"))

        val rootNode = parseStatement()
        return ParseTree(rootNode)
    }

//    fun isErrorFound(): Boolean = errorMsg.isNotEmpty()
//    fun getErrorMsgList(): List<String> = errorMsg.toList()
    private fun parseStatement(): Statement {
        return when (getCurToken().type){
            TokenType.SET -> parseSetStatement()
            TokenType.SHOW -> parseShowStatement()
            TokenType.IF -> parseIfStatement()
//            TokenType.WHILE -> parseWhileStatement()
            TokenType.OTHERWISE -> parseOtherwiseStatement()
            TokenType.BLOCK -> {
                consumeToken()
                expectToken(TokenType.EOL)
                consumeToken()
                Statement.Block()
            }
            TokenType.END_BLOCK, TokenType.END_WHILE, TokenType.END_IF -> {
                val endType = consumeToken().type
                expectToken(TokenType.EOL)
                Statement.End(endType)
            }
            else -> {
                throw Exception(createErrorMsg("Expected statement"))
            }
         }
    }

//    private fun parseWhileStatement(): Statement{
//        consumeToken()
//        val whileStatement = Statement.While(parseExpression())
//        expectToken(TokenType.DO)
//        consumeToken()
//        expectToken(TokenType.EOL)
//        return whileStatement
//    }


    private fun parseIfStatement(): Statement {
        consumeToken()
        val ifStatement = Statement.If(parseExpression())
        expectToken(TokenType.THEN)
        consumeToken()
        expectToken(TokenType.EOL)
        return ifStatement
    }

    private fun parseOtherwiseStatement(): Statement {
        consumeToken()
        if(getCurToken().type == TokenType.EOL) return Statement.Otherwise()

        expectToken(TokenType.IF)
        consumeToken()
        val otherwiseIfStatement = Statement.OtherwiseIf(parseExpression())
        expectToken(TokenType.THEN)
        consumeToken()
        expectToken(TokenType.EOL)
        return otherwiseIfStatement
    }


    private fun parseSetStatement(): Statement {
        consumeToken()

        expectToken(TokenType.IDENTIFIER)
        if (!getCurToken().lexeme.startsWith('$')) throw Exception(createErrorMsg("Variable name must start with $"))
        val identifierName = getCurToken().lexeme
        consumeToken()

        expectToken(TokenType.TO)
        consumeToken()


        val valueNode = parseExpression()
        expectToken(TokenType.EOL)
        return Statement.Set(identifierName, valueNode)
    }

    private fun parseShowStatement(): Statement {
        consumeToken()
        val valueNode = parseExpression()
        expectToken(TokenType.EOL)
        return Statement.Show(valueNode)
    }

    private fun parseExpression(): Node = orOperation()
    private fun orOperation(): Node {
        var leftNode = andOperation()

        while (getCurToken().type == TokenType.OR){
            consumeToken()
            if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing operand after ${TokenType.OR} operator"))

            val rightNode = andOperation()
            leftNode = Node.Binary(Operator.OR, leftNode, rightNode)
        }

        return leftNode
    }

    private fun andOperation(): Node {
        var leftNode = equalityOperation()

        while (getCurToken().type == TokenType.AND){
            consumeToken()
            if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing operand after ${TokenType.AND} operator"))

            val rightNode = equalityOperation()
            leftNode = Node.Binary(Operator.AND, leftNode, rightNode)
        }

        return leftNode
    }

    private fun equalityOperation(): Node {
        var leftNode = relationalOperator()

        while (true){
            val operator = when {
                checkTokenTypeSequence(TokenType.IS, TokenType.EQUAL, TokenType.TO) ->
                    Operator.EQUAL
                checkTokenTypeSequence(TokenType.IS, TokenType.NOT, TokenType.EQUAL, TokenType.TO) ->
                    Operator.NOT_EQUAL
                else -> break
            }
            if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing operand after $operator operator"))
            val rightNode = relationalOperator()

            leftNode = Node.Binary(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun relationalOperator(): Node {
        var leftNode = arithmeticOperation()

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
            if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing operand after $operator operator"))
            val rightNode = arithmeticOperation()

            leftNode = Node.Binary(operator, leftNode, rightNode)

        }

        return leftNode
    }

    private fun arithmeticOperation(): Node {
        var leftNode = unaryOperation()

        while (true) {
            val operator = when {
                checkTokenTypeSequence(TokenType.ADDED, TokenType.BY) -> Operator.ADD
                checkTokenTypeSequence(TokenType.SUBTRACTED, TokenType.BY) -> Operator.SUBTRACT
                checkTokenTypeSequence(TokenType.MULTIPLIED, TokenType.BY) -> Operator.MULTIPLY
                checkTokenTypeSequence(TokenType.DIVIDED, TokenType.BY) -> Operator.DIVIDE
                else -> break
            }
            if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing operand after $operator operator"))
            val rightNode = unaryOperation()

            leftNode = Node.Binary(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun unaryOperation(): Node {
        return if (getCurToken().type == TokenType.NOT){
            consumeToken()
            if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing operand after ${TokenType.NOT} operator"))
            val node = unaryOperation()
            Node.Unary(Operator.NOT, node)
        } else {
            literalValue()
        }
    }

    private fun literalValue(): Node {
        val node = when (getCurToken().type) {
            in setOf(TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN) -> {
                val literal = getCurToken().literal
                Node.Literal(literal, getCurToken().lineNumber)
            }
            in KeywordRegistry.getFunctionKeyword() -> {
                val functionName = getCurToken().type
                consumeToken()
                expectToken(TokenType.USING)
                consumeToken()

                val parameter = mutableListOf<Node>()
                if (getCurToken().type == TokenType.ONLY){
                    consumeToken()
                    parameter.add(parseExpression())
                } else {
                    while(true) {
                        if (getCurToken().type == TokenType.AND){
                            consumeToken()
                            parameter.add(parseExpression())
                            break
                        }
                        parameter.add(parseExpression())
                        expectToken(TokenType.COMMA)
                        consumeToken()
                    }
                }

                Node.Function(functionName, parameter, getCurToken().lineNumber)
            }
            TokenType.IDENTIFIER -> {
                Node.Variable(getCurToken().lexeme, getCurToken().lineNumber)
            }
            TokenType.OPEN_PARENTHESIS -> {
                consumeToken()
                if (!hasMoreTokens()) throw Exception(createErrorMsg("Missing expression after ${TokenType.OPEN_PARENTHESIS} symbol"))
                val innerNode = parseExpression()

                if (getCurToken().type != TokenType.CLOSE_PARENTHESIS) throw Exception(createErrorMsg("Expect ')' after expression"))
                else Node.Group(innerNode)
            }
            TokenType.EOL -> throw Exception(createErrorMsg("Improper code sequence"))
            else -> throw Exception(createErrorMsg("Unexpected token"))
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



//    private fun raiseParseError(errorMsg: String) = {
//        val completeErrorMsg = "Parsing Error: $errorMsg at line ${getCurToken().lineNumber}"
//        this.errorMsg.add(completeErrorMsg)
//        println(completeErrorMsg)
//    }
//    private fun failParser(errorMsg: String): Nothing? = raiseParseError(errorMsg).let { null }

    private fun createErrorMsg(errorContent: String): String = "Parsing Error: $errorContent at line ${getCurToken().lineNumber}"
    private fun hasMoreTokens(): Boolean = index < tokenList.size
    private fun getCurToken(): Token = tokenList[index]
    private fun consumeToken(): Token {
        return tokenList[index++]
    }
    private fun consumeToken(numOfTokensToConsume: Int) {
        index += numOfTokensToConsume
    }
    private fun expectToken(type: TokenType){
        if (getCurToken().type != type){
            throw Exception(createErrorMsg("Expected $type"))
        }
    }
}