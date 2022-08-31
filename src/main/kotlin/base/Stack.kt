package base

data class Stack(
    val pattern: MutableList<String> = arrayListOf(),
    val template: MutableList<String> = arrayListOf(),
    val that: MutableList<String> = arrayListOf()
)