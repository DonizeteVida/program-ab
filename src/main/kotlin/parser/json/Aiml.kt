package parser.json

data class Aiml(
    val description: String,
    val categories: List<Category>
)

data class Category(
    val type: String,
    val pattern: String,
    val template: String
)