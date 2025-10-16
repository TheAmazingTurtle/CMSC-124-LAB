class CodeFile(){
    private val fileLexer = Lexer()
    private val fileParser = Parser()


    fun getLine(content: String): Line{
        return fileLexer.getNewLine(content)
    }

    fun getParseTree(content: String): ParseTree {
        val newLine = getLine(content)

        for (token in newLine.tokenList){
            if (token.type == "ERROR"){
                
            }
        }

        return fileParser.parseLine(newLine.tokenList)
    }
}