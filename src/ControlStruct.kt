sealed class ControlStruct {
    data class If(var conditionSatisfied: Boolean = false, var trueBlockExecuted: Boolean = false): ControlStruct()

}