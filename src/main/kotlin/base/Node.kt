package base

interface Node {
    val index: String
    val nodes: HashMap<String, Node>

    operator fun get(index: String): Node?
    operator fun set(index: String, node: Node)
    operator fun plusAssign(other: Node)

    data class Complete(
        override val index: String,
        override val nodes: HashMap<String, Node> = hashMapOf(),
        val template: String,
        val commands: List<String> = arrayListOf(),
        val context: HashMap<String, Node> = hashMapOf()
    ) : Node {
        override fun get(index: String) = nodes[index]

        override fun set(index: String, node: Node) {
            nodes[index] = node
        }

        override fun plusAssign(other: Node) {
            when (other) {
                is Complete -> {
                    commands.plus(other.commands)
                    context.plus(other.context)
                    println("A complete node was removed: $other")
                }
                is Incomplete -> println("An incomplete node was removed: $other")
            }
            nodes.plus(other.nodes)
        }
    }

    data class Incomplete(
        override val index: String,
        override val nodes: HashMap<String, Node> = hashMapOf()
    ) : Node {
        override fun get(index: String) = nodes[index]

        override fun set(index: String, node: Node) {
            nodes[index]?.apply(node::plusAssign)
            nodes[index] = node
        }

        override fun plusAssign(other: Node) {
            nodes.plus(other.nodes)
            if (other is Complete) {
                throw IllegalStateException("A complete node was removed in favor of an incomplete:\nis $this\n'll be $other")
            }
        }
    }
}