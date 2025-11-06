fun main(){
    val lexer = Lexer()
    val parser = Parser()
    val evaluator = Evaluator()

    while (true) {
        print("> ")
        val userInput = readln().trim()


        val tokens = lexer.getTokensFromLine(userInput) ?: continue
        tokens.forEach { println(it) }

        val parseTree = parser.getParseTree(tokens) ?: continue
        // println(parseTree)

        val result = evaluator.getValueOfParseTree(parseTree) ?: continue
        println(result)

    }
}