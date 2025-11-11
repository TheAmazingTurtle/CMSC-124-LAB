fun main(){
    val lexer = Lexer()
    val parser = Parser()
    val evaluator = Evaluator()

    while (true) {
        print("> ")
        val userInput = readln().trim()
        if (userInput.isEmpty()) continue

        try {
            val tokens = lexer.getTokensFromLine(userInput)
            tokens.forEach { println(it) }

            val parseTree = parser.getParseTree(tokens)
            println(parseTree)

            val result = evaluator.getValueOfParseTree(parseTree)
            println(result)
        } catch (e: Exception) {
            println(e.message)
        }

    }
}