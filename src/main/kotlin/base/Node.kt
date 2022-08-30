package base

data class Node(
    val pattern: String,
    val template: String,
    val children: HashMap<String, Node> = hashMapOf()
) {
    operator fun get(index: String) = children[index]

    operator fun set(index: String, node: Node) {
        children[index] = node
    }

    val isWildCard: Boolean
        get() = "*" == pattern
}