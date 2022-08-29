package base

import parser.json.Aiml

class NodeManager private constructor(
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

    companion object {
        fun <T> build(builder: Builder<T>) = builder()
    }

    sealed interface Builder<T> : () -> NodeManager {
        data class JsonBuilder(
            val data: List<Aiml>
        ) : Builder<List<Aiml>> {
            private fun buildNodeTree(
                actual: Node,
                prev: Node?,
                args: List<String>,
                cursor: Int
            ): Pair<Node, Node?> {
                if (cursor + 1 !in args.indices) return actual to prev
                val pattern = args[cursor + 1]
                val next = actual[pattern] ?: Node(
                    pattern,
                    IncompleteResponse
                )
                actual[pattern] = next
                return buildNodeTree(next, actual, args, cursor + 1)
            }

            override fun invoke(): NodeManager {
                val nodes = hashMapOf<String, Node>()

                data.map(Aiml::categories).flatten().map {
                    val args = it.pattern.split(" ")
                    val pattern = args[0]
                    val parent = nodes[pattern] ?: Node(
                        pattern,
                        IncompleteResponse
                    )
                    nodes[pattern] = parent
                    val (actual, prev) = buildNodeTree(parent, null, args, 0)
                    if (prev == null) {
                        nodes[pattern] = actual.copy(
                            response = ConcreteResponse(it.template)
                        )
                    } else {
                        prev[args.last()] = actual.copy(
                            response = ConcreteResponse(it.template)
                        )
                    }
                }

                return NodeManager(nodes)
            }
        }
    }
}