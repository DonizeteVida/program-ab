package base

data class Stack(
    val template: MutableList<String> = arrayListOf(),
    val that: MutableList<String> = arrayListOf()
)