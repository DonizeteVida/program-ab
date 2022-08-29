package parser.xml

data class Aiml(
    var category: List<Category>
)

data class Category(
    var pattern: String,
    var template: String
)