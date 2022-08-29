package base

class NodeManager(
    val nodes: HashMap<String, Node> = hashMapOf()
) {
    fun find(pattern: String): String {
        val split = pattern.split(" ")
        return internalFind(split[0], split.drop(1))
    }

    private fun internalFind(pattern: String, split: List<String>): String {
        val node = nodes[pattern] ?: nodes["*"] ?: throw IllegalStateException("A default response must be provided")
        if (split.isEmpty()) return node.response()
        val nextPattern = split[0]
        return findLastNode(node, nextPattern, split.drop(1)).response()
    }

    private fun findLastNode(node: Node, pattern: String, split: List<String>): Node {
        val nextNode = node.children[pattern] ?: node.children["*"]
        if (nextNode == null || split.isEmpty()) return node
        val nextPattern = split[0]
        return findLastNode(nextNode, nextPattern, split.drop(1))
    }
}