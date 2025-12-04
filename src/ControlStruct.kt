

sealed class ControlStruct {
    data class If(var conditionSatisfied: Boolean = false, var trueBlockExecuted: Boolean = false, var otherwiseSeen: Boolean = false): ControlStruct()

    data class While( val booleanCondition: Node, var recordingDone: Boolean = false, val statementRecord: MutableList<Statement> = mutableListOf()): ControlStruct()
}