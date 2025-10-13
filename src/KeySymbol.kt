enum class KeySymbol(val symbol: String, ) {
    // Logical Operators
    LESSER("<"),
    GREATER(">"),
    LESSER_EQUAL("<="),
    GREATER_EQUAL(">="),
    EQUAL("=="),
    NOT("!"),
    AND("&&"),
    OR("||"),

    // Assignment Operators
    ASSIGN("="),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    DIVIDE_ASSIGN("/="),
    MODULO_ASSIGN("%="),

    // Arithmetic Operators
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    POWER ("**"),

    // INCR/DECR
    INCREMENT("++"),
    DECREMENT("--"),

    // Delimiters
    L_PAREN("("),
    R_PAREN(")"),
    L_BRACKET("["),
    R_BRACKET("]"),
    L_BRACE("{"),
    R_BRACE("}"),
    COMMA(","),
    DOT("."),

    // Comment
    ONE_LINE_COMMENT("//"),
    MULTI_LINE_COMMENT_OPEN("/*"),
    MULTI_LINE_COMMENT_CLOSE("/*")
}