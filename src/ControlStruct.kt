import java.lang.reflect.Parameter

sealed class ControlStruct: Executable() {
    abstract fun recordExecutable(executable: Executable)

    data class If(private val condition: Node, private val branchList: MutableList<Branch> = mutableListOf(Branch(condition)), var otherwiseEncountered: Boolean = false): ControlStruct() {

        data class Branch(val condition: Node, val executables: MutableList<Executable> = mutableListOf())

        override fun recordExecutable(executable: Executable) {
            getCurBranch().executables.add(executable)
        }
        fun getCurBranch(): Branch = branchList.last()
        fun getBranches(): List<Branch> = branchList
        fun addBranch(condition: Node) = branchList.add(Branch(condition))
    }

    data class Block(val executables: MutableList<Executable> = mutableListOf()): ControlStruct() {
        override fun recordExecutable(executable: Executable) {
            executables.add(executable)
        }
    }

     data class While(val condition: Node, val executables: MutableList<Executable> = mutableListOf()): ControlStruct() {
         override fun recordExecutable(executable: Executable) {
             executables.add(executable)
         }
     }

    data class Function(val name: String, val parameterName: List<String>, val executables: MutableList<Executable> = mutableListOf()): ControlStruct() {
        data class Parameter(val name: String, var value: Any)

        val parameterList = parameterName.map { Parameter(it, Unit) }.toMutableList()

        override fun recordExecutable(executable: Executable) {
            executables.add(executable)
        }
    }
}