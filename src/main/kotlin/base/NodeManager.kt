package base

class NodeManager(
    private val nodes: HashMap<String, Node> = hashMapOf()
) {
    fun find(pattern: String): String {
        val args = pattern.split(" ")
        if (args.isEmpty()) throw IllegalStateException("A pattern must be provided")
        return internalFind(args)
    }

    private fun internalFind(args: List<String>): String {
        val node = nodes[args[0]] ?: nodes["*"] ?: throw IllegalStateException("A default response must be provided")
        return findLastNode(node, args, 0).response()
    }

    private fun findLastNode(node: Node, args: List<String>, cursor: Int): Node {
        if (cursor + 1 !in args.indices) return node
        val pattern = args[cursor + 1]
        val next = node.children[pattern] ?: node.children["*"] ?: return node
        return findLastNode(next, args, cursor + 1)
    }

    operator fun get(index: String) = nodes[index]

    operator fun set(index: String, node: Node) {
        nodes[index] = node
    }
}