class CodeFile(){
    private val fileLexer = Lexer()
    private val fileParser = Parser()
    private val lineList = mutableListOf<Line>()

    fun addLine(content: String){
        val newLine = fileLexer.getNewLine(content)
        lineList.add(newLine)
        fileParser.parseLine(newLine)
    }

    fun getCurLine(): Line{
        return lineList[lineList.size-1]
    }

    fun displayTokens(lineNumber: Int ){
        for (token in lineList[lineNumber-1].tokenList){
            println("Token(type=${token.type}, lexeme=${token.lexeme}, literal=${token.literal}, line=${token.lineNumber})")
        }
    }

    fun displayAllTokens(){
        for (line in lineList){
            for (token in line.tokenList){
                println("Token(type=${token.type}, lexeme=${token.lexeme}, literal=${token.literal}, line=${token.lineNumber})")
            }
        }
    }
}