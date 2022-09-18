package parser.json

data class Aiml(
    val description: String = "",
    val incompletePattern: String = "",
    val variables: Map<String, String> = emptyMap(),
    val categories: List<Category> = emptyList(),
    val sets: Map<String, List<String>> = emptyMap()
)

data class Category(
    val pattern: String,
    val template: String,
    val commands: List<String>?,
    val that: String?
)