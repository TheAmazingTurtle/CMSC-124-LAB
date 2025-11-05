fun main(){
    val lexer = Lexer()
    val parser = Parser()
    val evaluator = Evaluator()


    while (true) {
        print("> ")
        val userInput = readln().trim()


        val tokens = lexer.getTokensFromLine(userInput)
        if (lexer.isErrorFound()){
            println(lexer.getErrorMsg())
            continue
        }
        tokens.forEach { println(it) }

        val parseTree = parser.getParseTree(tokens)
        if (parser.isErrorFound()) {
            parser.getErrorMsgList().forEach { println(it) }
            continue
        }
        println(parseTree)

        val result = evaluator.getValueOfParseTree(parseTree)
        if (evaluator.isErrorFound()) {
            evaluator.getErrorMsgList().forEach { println(it) }
            continue
        }
        else println(result)

    }
}