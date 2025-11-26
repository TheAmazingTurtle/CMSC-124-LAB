class Environment {
    private val values = mutableMapOf<String, Any>()

    fun define(name:String, value: Any) {values[name] = value}
    fun getName(name:String): Any? = values[name]
//    fun assign(name:String, value: Any){
//        if(name in values) values[name] = value
//        else throw Exception("Cannot assign to undefined variable $name")
//    }
}