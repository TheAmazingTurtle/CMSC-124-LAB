fun main(){
    val lexer = Lexer()


    while (true) {
        print("> ")
        val userInput = readln()

        val tokens = lexer.getTokens(userInput)
    }
}