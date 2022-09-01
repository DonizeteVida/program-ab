package base

import base.postprocessor.StringPostProcessor
import parser.json.Aiml

class NodeManager private constructor(
    private val nodes: HashMap<String, Node> = hashMapOf(),
    private val transformManager: TransformManager
) {
    fun find(pattern: String): String {
        val args = pattern.split(" ")
        if (args.isEmpty()) throw IllegalStateException("A pattern must be provided")
        val stack = Stack()
        val node = internalFind(args, stack) ?: return "Logic not implemented yet"
        return when (val result = transformManager.transform(node, stack)) {
            is StringPostProcessor.Result.Finish -> result.string
            is StringPostProcessor.Result.Rerun -> find(result.string)
            is StringPostProcessor.Result.Success -> throw IllegalStateException("Success result should be handled internally")
        }
    }

    private fun internalFind(args: List<String>, stack: Stack): Node? {
        val pattern = args[0]
        val indices = args.indices
        val node = nodes[pattern] ?: nodes["*"] ?: throw IllegalStateException("A default response must be provided")
        return findLastNode(node, pattern, args, stack, indices, 0)
    }

    private fun findLastNode(
        node: Node,
        pattern: String,
        args: List<String>,
        stack: Stack,
        indices: IntRange,
        cursor: Int
    ): Node? {
        val hasNextArg = cursor + 1 in indices
        if (node.isWildCard) {
            if (!hasNextArg) {
                stack.star += pattern
                stack.pattern += pattern
            } else {
                val matches = arrayListOf(pattern)
                var allowCardCursor = cursor + 1
                var patternLookahead = args[allowCardCursor]
                var lookahead: Node? = node.children[patternLookahead]
                while (lookahead == null) {
                    matches += patternLookahead
                    if (++allowCardCursor !in indices) break
                    patternLookahead = args[allowCardCursor]
                    lookahead = node.children[patternLookahead]
                }
                val arg = matches.joinToString(" ")
                stack.star += arg
                stack.pattern += arg
                return if (lookahead == null) {
                    node
                } else {
                    findLastNode(lookahead, patternLookahead, args, stack, indices, allowCardCursor)
                }
            }
        } else {
            stack.pattern += pattern
        }
        if (!hasNextArg) return node
        val nextPattern = args[cursor + 1]
        val next = node.children[nextPattern] ?: node.children["*"] ?: return null
        return findLastNode(next, nextPattern, args, stack, indices, cursor + 1)
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
                indices: IntRange,
                cursor: Int
            ): Pair<Node, Node?> {
                if (cursor + 1 !in indices) return actual to prev
                val pattern = args[cursor + 1]
                val next = actual[pattern] ?: Node(
                    pattern
                ).also {
                    actual[pattern] = it
                }
                return buildNodeTree(next, actual, args, indices, cursor + 1)
            }

            override fun invoke(): NodeManager {
                val nodes = hashMapOf<String, Node>()
                val memory = Memory()
                val transformManager = TransformManager(memory)

                data.map(Aiml::variables).forEach {
                    it.forEach { (key, value) ->
                        memory.variables[key] = value
                    }
                }

                data.map(Aiml::categories).flatten().map {
                    val args = it.pattern.split(" ")
                    val pattern = args[0]
                    val node = nodes[pattern] ?: Node(
                        pattern
                    ).also { node ->
                        nodes[pattern] = node
                    }
                    val (actual, prev) = buildNodeTree(node, null, args, args.indices, 0)
                    val finally = actual.copy(
                        template = it.template,
                        commands = it.commands ?: emptyList()
                    )
                    if (prev == null) {
                        nodes[pattern] = finally
                    } else {
                        prev[finally.pattern] = finally
                    }
                }

                return NodeManager(nodes, transformManager)
            }
        }
    }
}