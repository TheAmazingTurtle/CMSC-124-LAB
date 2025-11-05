fun main(){
    val lexer = Lexer()
    val parser = Parser()

    while (true) {
        print("> ")
        val userInput = readln()

        val tokens = lexer.getTokensFromLine(userInput)
        if (lexer.isErrorFound()){
            println(lexer.getErrorMsg())
            continue
        }

        for (token in tokens){
            println(token)
        }

        val parseTree = parser.getParseTree(tokens)
        if (parser.isErrorFound()){
            for (errorMsg in parser.getErrorMsgList()){
                println(errorMsg)
            }
            continue
        }

        println(parseTree)

        val result = Evaluator().getValueOfParseTree(parseTree)
        println(result)

    }
}