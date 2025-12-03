sealed class Statement{
    data class SetVariable(val name: String, val value: Node): Statement()
    data class Show(val value: Node): Statement()
    data class Block(val enterBlock: Boolean = true): Statement()
//    data class IfStmt(
//        val condition: Node,
//        val thenBlock: List<Stmt>,
//        val otherwiseBlock: List<Stmt>? = null
//    ): Stmt()
    //    data class SaveStmt(): Stmt()
    //    data class SetFuncStmt(): Stmt()
    //    data class SetDataStrctStmt(): Stmt()
    //    data class CrtTableStmt(): Stmt()
    //    data class InsertStmt(): Stmt()
    //    data class WhileStmt(): Stmt()
    //    data class DoWhileStmt(): Stmt()
    //    data class ForStmt(): Stmt()
    //    data class BasedOnStmt(): Stmt()
}