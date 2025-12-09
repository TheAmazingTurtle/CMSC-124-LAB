fun main(args: Array<String>) {
    val lexer = Lexer()
    val parser = Parser()
    val environment = Environment()
    val evaluator = Evaluator(environment)

    if (args.isNotEmpty()) {
        val filePath = args[0]
        val lines = java.io.File(filePath).readLines()

        for ((index, line) in lines.withIndex()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            try {
                val tokens = lexer.getTokensFromLine(trimmed, index+1, true)
                val parseTree = parser.getParseTree(tokens)
                evaluator.evaluateParseTree(parseTree)
            } catch (e: Exception) {
                println("Error in file: ${e.message}")
                return
            }
        }
        return
    }

    while (true) {
        print("> ")
        val userInput = readln().trim()
        if (userInput.isEmpty()) continue

        try {
            val tokens = lexer.getTokensFromLine(userInput)
            val parseTree = parser.getParseTree(tokens)
            evaluator.evaluateParseTree(parseTree)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}


