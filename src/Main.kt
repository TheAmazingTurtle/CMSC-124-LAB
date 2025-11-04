fun main(){
    val lexer = Lexer()

    var lineNumber = 0
    while (true) {
        lineNumber++

        print("> ")
        val userInput = readln()

        val tokens = lexer.getTokensFromLine(userInput, lineNumber)
        if (lexer.isErrorFound()){
            println(lexer.getErrorMsg())
            continue
        }

        for (token in tokens){
            println(token)
        }
    }
}