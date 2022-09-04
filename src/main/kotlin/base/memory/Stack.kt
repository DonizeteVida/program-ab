package base.memory

data class Stack(
    val pattern: MutableList<String> = arrayListOf(),
    val star: MutableList<String> = arrayListOf(),
    val that: MutableList<String> = arrayListOf()
)