import jdk.dynalink.Operation

class Parser() {
    var exprTree = Tree()
    var curNode: ExpressionNode? = null

    var index = 0
    var curLine: Line? = null
    var curToken: Token? = null
    var nextToken: Token? = null

    fun parseLine(line: Line){
        index = 0

        curLine = line
        curToken = curLine?.tokenList[index]
        nextToken = curLine?.tokenList[index+1]

        evaluateExpression()
    }

    fun evaluateExpression(): ExpressionNode{
        exprTree(when {
            logicExpression()
            arithmeticExpression()
        })

        // parsing error
    }

    fun logicExpression(): ExpressionNode{
        orOperation()
    }

    fun orOperation(): ExpressionNode{
        andOperation()
    }

    fun andOperation(): ExpressionNode {
        equalityOperation()
    }

    fun equalityOperation(): ExpressionNode {
        relationalOperation()
    }

    fun relationalOperation(): ExpressionNode {
        val leftNode = notOperation()

        if (curToken?.type in setOf("LESSER", "GREATER", "LESSER_EQUAL", "GREATER_EQUAL")){
            val operator = curToken?.lexeme
            lex()

            return BinaryNode(curNode, operator?:"", leftNode, notOperation())
        }

        return leftNode
    }

    fun notOperation(): ExpressionNode {
        if (curToken?.type == "NOT"){
            lex()
            return UnaryNode(curNode, "!", notOperation())
        }
        else {
            return term()
        }

    }

    fun term(): ExpressionNode {
        val node = when (curToken?.type){
                        "TRUE"      -> LiteralNode(curNode, true)
                        "FALSE"     -> LiteralNode(curNode, false)
                        "L_PAREN"   -> GroupNode(curNode, evaluateExpression())
                        else        -> LiteralNode(curNode, "ERROR") // error
                    }
        lex()
        return node
    }

    fun arithmeticExpression() : Boolean{

    }

    fun lex(){
        index++
        curToken = curLine?.tokenList[index]
        nextToken = curLine?.tokenList[index+1]
    }

}