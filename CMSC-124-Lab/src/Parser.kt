class Parser() {
    var exprTree = Tree()
    var curExpr: Expression? = null

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

    fun evaluateExpression(){
        when {
            logicExpression()       -> return
            arithmeticExpression()  -> return
        }

        // parsing error
    }

    fun logicExpression(): Boolean{
        orOperation()
    }

    fun orOperation(){
        andOperation()
    }

    fun andOperation(){
        equalityOperation()
    }

    fun equalityOperation(){
        relationalOperation()
    }

    fun relationalOperation(){
        notOperation()
    }

    fun notOperation(){
        term()
    }

    fun term(){
        when (curToken?.type){
            "TRUE"      -> return
            "FALSE"     -> return
            "L_PAREN"   -> evaluateExpression()
        }
    }

    fun arithmeticExpression() : Boolean{

    }

    fun lex(){
        index++
        curToken = curLine?.tokenList[index]
        nextToken = curLine?.tokenList[index+1]
    }

}