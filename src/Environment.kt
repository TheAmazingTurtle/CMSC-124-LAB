import kotlin.collections.mutableMapOf

class Environment {
    private val globalEnvironment = mutableMapOf<String, Any>()
    private val environmentStack = mutableListOf(globalEnvironment)

    private fun getCurEnvironment() = environmentStack.last()

    fun define(name:String, value: Any) {
        for (environment in environmentStack) {
            if (environment.containsKey(name)) {
                environment[name] = value
                return
            }
        }
        getCurEnvironment()[name] = value
    }

    fun getValue(name:String): Any? {
        for (environment in environmentStack) {
            if (environment.containsKey(name)) {
                return environment[name]
            }
        }

        return null
    }

    fun createInnerEnvironment() {
        environmentStack.add(mutableMapOf())
    }

    fun destroyInnerEnvironment() {
        if (environmentStack.size == 1) throw Exception("Runtime Error: Unexpected end_block statement")
        environmentStack.removeLast()
    }


//    fun assign(name:String, value: Any){
//        if(name in values) values[name] = value
//        else throw Exception("Cannot assign to undefined variable $name")
//    }
}