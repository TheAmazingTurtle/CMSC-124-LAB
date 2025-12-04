import javax.xml.xpath.XPathExpression

sealed class Statement{
    data class SetVariable(val name: String, val value: Node): Statement()
    data class Show(val value: Node): Statement()
    data class Block(val enterBlock: Boolean = true): Statement()

//    data class If(var conditionSatisfied: Boolean = false, var trueBlockExecuted: Boolean = false, val expression: Node): Statement()
    data class If(val expression: Node): Statement()
    data class OtherwiseIf(val expression: Node): Statement()
    data class Otherwise(val expression: Nothing?): Statement()
    data class EndIf(val content: Nothing? = null):Statement()
//    data class WhileStmt(): Stmt()
//    data class ForStmt(): Stmt()

//    data class IfStmt(
//        val condition: Node,
//        val thenBlock: List<Stmt>,
//        val otherwiseBlock: List<Stmt>? =  null
//    ): Stmt()
    //    data class SaveStmt(): Stmt()
    //    data class SetFuncStmt(): Stmt()
    //    data class SetDataStrctStmt(): Stmt()
    //    data class CrtTableStmt(): Stmt()
    //    data class InsertStmt(): Stmt()
    //    data class DoWhileStmt(): Stmt()
    //    data class BasedOnStmt(): Stmt()
}