package base

import base.response.Response
import parser.json.Aiml

class NodeManager private constructor(
    private val nodes: HashMap<String, Node> = hashMapOf(),
    private val memory: Memory = Memory()
) {
    fun find(pattern: String): String {
        val args = pattern.split(" ")
        if (args.isEmpty()) throw IllegalStateException("A pattern must be provided")
        val stack = Stack()
        val node = internalFind(args, stack)
        return Response.transform(node.template, stack, memory)
    }

    private fun internalFind(args: List<String>, stack: Stack): Node {
        val pattern = args[0]
        val node = nodes[pattern] ?: nodes["*"] ?: throw IllegalStateException("A default response must be provided")
        return findLastNode(node, args, stack, 0)
    }

    private fun findLastNode(node: Node, args: List<String>, stack: Stack, cursor: Int): Node {
        if (node.isWildCard) {
            stack.template += args[cursor]
        }
        if (cursor + 1 !in args.indices) return node
        val pattern = args[cursor + 1]
        val next = node.children[pattern] ?: node.children["*"] ?: return node
        return findLastNode(next, args, stack, cursor + 1)
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
                    ""
                )
                actual[pattern] = next
                return buildNodeTree(next, actual, args, cursor + 1)
            }

            override fun invoke(): NodeManager {
                val nodes = hashMapOf<String, Node>()

                data.map(Aiml::categories).flatten().map {
                    val args = it.pattern.split(" ")
                    val pattern = args[0]
                    val node = nodes[pattern] ?: Node(
                        pattern,
                        ""
                    )
                    nodes[pattern] = node
                    val (actual, prev) = buildNodeTree(node, null, args, 0)
                    if (prev == null) {
                        nodes[pattern] = actual.copy(
                            template = it.template
                        )
                    } else {
                        prev[args.last()] = actual.copy(
                            template = it.template
                        )
                    }
                }

                return NodeManager(nodes)
            }
        }
    }
}