fun main() {
    val mainFile = CodeFile()

    while (true) {
        print(">> ")
        val userInput = readlnOrNull() ?: ""

        println(mainFile.getParseTree(userInput))
    }
}
