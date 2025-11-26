//if this says kotlin not configured again,
// fix project sdk and module sdk
// and set to 21 in project structure settings

fun main(){
    val lexer = Lexer()
    val parser = Parser()
    val env = Environment()
    val evaluator = Evaluator(env)

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