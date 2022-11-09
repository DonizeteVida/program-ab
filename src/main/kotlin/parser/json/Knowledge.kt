package parser.json

data class Knowledge(
    val description: String = "",
    val incompletePattern: String = "",
    val variables: Map<String, String> = emptyMap(),
    val categories: List<Category> = emptyList(),
    val sets: Map<String, List<String>> = emptyMap()
)

data class Category(
    val id: String?,
    val pattern: String,
    val template: String,
    val commands: List<String>?,
    val context: Context?
)

data class Context(
    val id: String
)