package base.memory

data class Stack(
    val pattern: ArrayList<String> = arrayListOf(),
    val star: ArrayList<String> = arrayListOf(),
    val context: ArrayList<String> = arrayListOf()
)