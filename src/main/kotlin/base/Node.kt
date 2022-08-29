package base

data class Node(
    val parent: Node? = null,
    val pattern: String,
    val response: Response,
    val children: HashMap<String, Node> = hashMapOf()
)