class Scanner(private var lineString: String = "") {
    private var curIndex = 0
    private var startIndex = 0

    val curChar: Char? get() = lineString.getOrNull(curIndex)
    val nextChar: Char? get() = lineString.getOrNull(curIndex + 1)

    fun setLineString(newLineString: String) {
        lineString = newLineString
        curIndex = 0
        startIndex = 0
    }

    fun markStart() { startIndex = curIndex }

    fun getSubstring(): String = lineString.substring(startIndex, curIndex)

    fun advance(increment: Int = 1) { curIndex += increment}

    fun hasCharsLeft(): Boolean = curIndex < lineString.length
}