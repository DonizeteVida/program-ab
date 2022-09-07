package base.processor

import base.Node
import base.memory.Memory
import base.memory.Stack
import base.processor.command.CommandPostNodeProcessorImpl
import base.processor.template.TemplatePostNodeProcessorImpl
import base.processor.template.TemplatePostProcessor
import base.regex.RegexPattern
import parser.json.Aiml
import parser.json.Category

class NodeManager private constructor(
    private val nodes: HashMap<String, Node> = hashMapOf(),
    private val templatePostNodeProcessor: NodeProcessor<TemplatePostProcessor.Result>,
    private val commandPostNodeProcessor: NodeProcessor<Unit>
) {
    fun find(pattern: String): String {
        val args = pattern.split(" ")
        if (args.isEmpty()) throw IllegalStateException("A pattern must be provided")
        val stack = Stack()
        val node = internalFind(args, stack) ?: return "Logic not implemented yet"
        commandPostNodeProcessor(node, stack)
        return when (val result = templatePostNodeProcessor(node, stack)) {
            is TemplatePostProcessor.Result.Finish -> result.string
            is TemplatePostProcessor.Result.Rerun -> find(result.string)
            is TemplatePostProcessor.Result.Success -> throw IllegalStateException("Success result should be handled internally")
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
                var lookahead: Node? = node[patternLookahead]
                while (lookahead == null) {
                    matches += patternLookahead
                    if (++allowCardCursor !in indices) break
                    patternLookahead = args[allowCardCursor]
                    lookahead = node[patternLookahead]
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
        val next = node[nextPattern] ?: node["*"] ?: return null
        return findLastNode(next, nextPattern, args, stack, indices, cursor + 1)
    }

    companion object {
        private fun buildNodeTree(
            last: Node,
            parent: Node?,
            args: List<String>,
            indices: IntRange,
            cursor: Int
        ): Pair<Node, Node?> {
            if (cursor + 1 !in indices) return last to parent
            val nextPattern = args[cursor + 1]
            val next = last[nextPattern] ?: Node(
                nextPattern
            ).also {
                last[nextPattern] = it
            }
            return buildNodeTree(next, last, args, indices, cursor + 1)
        }

        private fun expandCategoryIfNecessary(category: Category, aiml: Aiml): List<Category> {
            //sorry gods of code optimization
            //I, honestly, don't know to make it better
            val pattern = category.pattern
            val matches = RegexPattern.SET.findAll(pattern)
            if (!matches.any()) return arrayListOf(category)
            val expanded = arrayListOf(pattern)
            for (m in matches) {
                val all = m.groups[0]!!.value
                val variable = m.groups[1]!!.value
                val sets = aiml.sets[variable] ?: throw IllegalStateException("Set $variable not found")
                sets.map { set ->
                    expanded.map { pattern ->
                        pattern.replace(all, set)
                    }
                }.flatten().also {
                    expanded.clear()
                    expanded.addAll(it)
                }
            }
            return expanded.map {
                category.copy(
                    pattern = it
                )
            }
        }

        fun build(data: List<Aiml>): NodeManager {
            val nodes = hashMapOf<String, Node>()
            val memory = Memory()
            val templatePostNodeProcessor = TemplatePostNodeProcessorImpl(memory)
            val commandPostProcessor = CommandPostNodeProcessorImpl(memory)

            data.map(Aiml::variables).forEach(memory.variables::putAll)

            data
                .map { aiml ->
                    aiml.categories.map {
                        expandCategoryIfNecessary(it, aiml)
                    }
                }
                .flatten()
                .flatten()
                .forEach {
                    val args = it.pattern.split(" ")
                    val pattern = args[0]
                    val node = nodes[pattern] ?: Node(
                        pattern
                    ).also { node ->
                        nodes[pattern] = node
                    }
                    val (last, parent) = buildNodeTree(node, null, args, args.indices, 0)
                    val lastCopy = last.copy(
                        template = it.template,
                        commands = it.commands ?: emptyList()
                    )
                    if (parent == null) {
                        nodes[pattern] = lastCopy
                    } else {
                        parent[lastCopy.pattern] = lastCopy
                    }
                }

            return NodeManager(nodes, templatePostNodeProcessor, commandPostProcessor)
        }
    }
}