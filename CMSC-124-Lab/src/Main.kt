import kotlin.collections.mutableListOf
import kotlin.system.exitProcess
import kotlin.text.isDigit
import kotlin.text.isLetter
import kotlin.text.isLetterOrDigit
import kotlin.text.isWhitespace



fun main() {
    val mainFile = CodeFile()

    while (true) {
        print(">> ")
        val userInput = readlnOrNull() ?: ""

        mainFile.addLine(userInput)
        for (token in mainFile.getCurLine().tokenList) {
            println(token)
        }
    }
}
