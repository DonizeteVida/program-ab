package parser.json

data class Aiml(
    val description: String,
    val incompletePattern: String,
    val variables: HashMap<String, String>,
    val categories: List<Category>
)

data class Category(
    val pattern: String,
    val template: String,
    val commands: List<String>?
)