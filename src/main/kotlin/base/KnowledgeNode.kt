package base

data class KnowledgeNode(
    val pattern: String,
    val template: String,
    val commands: List<String> = arrayListOf(),
    val thats: HashMap<String, KnowledgeNode> = hashMapOf(),
    val children: HashMap<String, KnowledgeNode> = hashMapOf()
) : Node<KnowledgeNode> {
    override operator fun get(index: String) = children[index]

    override fun plusAssign(other: KnowledgeNode) {
        children += other.children
    }

    override operator fun set(index: String, node: KnowledgeNode) {
        children[index] = node
    }

    val isWildCard
        get() = "*" == pattern
}