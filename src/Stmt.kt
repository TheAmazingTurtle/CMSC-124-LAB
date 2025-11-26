sealed class Stmt {
    data class SetVarStmt(val name: String, val value: Node): Stmt()
    data class ShowStmt(val value: Node): Stmt()
    data class IfStmt(
        val condition: Node,
        val thenBlock: List<Stmt>,
        val otherwiseBlock: List<Stmt>? = null
    ): Stmt()
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