package base

data class KnowledgeNode(
    val pattern: String,
    val template: String,
    val commands: List<String> = arrayListOf(),
    val contextualNodes: HashMap<String, KnowledgeNode> = hashMapOf(),
    val knowledgeNodes: HashMap<String, KnowledgeNode> = hashMapOf()
) : Node<KnowledgeNode>, NodeManager<KnowledgeNode> {
    override operator fun get(index: String) = knowledgeNodes[index]

    override fun plusAssign(other: KnowledgeNode) {
        knowledgeNodes += other.knowledgeNodes
    }

    override operator fun set(index: String, node: KnowledgeNode) {
        knowledgeNodes[index] = node
    }

    val isWildCard
        get() = "*" == pattern
}