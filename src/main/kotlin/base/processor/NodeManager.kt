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
        val node = internalFind(args, stack) ?: throw IllegalStateException("Partial matching not implemented yet")
        commandPostNodeProcessor(node, stack)
        return when (val result = templatePostNodeProcessor(node, stack)) {
            is TemplatePostProcessor.Result.Finish -> result.string
            is TemplatePostProcessor.Result.Rerun -> find(result.string)
            is TemplatePostProcessor.Result.Success -> throw IllegalStateException("Success result should be handled internally")
        }
    }

    private fun internalFind(args: List<String>, stack: Stack): Node? {
        val arg = args[0]
        val indices = args.indices
        val node = nodes[arg] ?: nodes["*"] ?: throw IllegalStateException("A default response must be provided")
        return findLastNode(node, arg, args, stack, indices, 0)
    }

    private fun findLastNode(
        node: Node,
        arg: String,
        args: List<String>,
        stack: Stack,
        indices: IntRange,
        cursor: Int
    ): Node? {
        val nextCursor = cursor + 1
        val hasNextArg = nextCursor in indices
        if (node.isWildCard) {
            if (hasNextArg) {
                //wild card lookahead logic
                val wildMatches = arrayListOf(arg)
                var wildCursor = cursor + 1
                var nextArg = args[wildCursor]
                var nextNode = node[nextArg]
                //if next node equals null
                //we should use current nextArg as wild matching argument
                while (nextNode == null) {
                    wildMatches += nextArg
                    if (++wildCursor !in indices) break
                    nextArg = args[wildCursor]
                    nextNode = node[nextArg]
                }
                val actualArg = wildMatches.joinToString(" ")
                stack.star += actualArg
                stack.pattern += actualArg
                return if (nextNode == null) {
                    node
                } else {
                    findLastNode(nextNode, nextArg, args, stack, indices, wildCursor)
                }
            }
            stack.star += arg
        }
        stack.pattern += arg
        if (!hasNextArg) return node
        val nextArg = args[nextCursor]
        val nextNode = node[nextArg] ?: node["*"] ?: return null
        return findLastNode(nextNode, nextArg, args, stack, indices, nextCursor)
    }

    companion object {
        private fun buildNodeTree(
            currentNode: Node, parentNode: Node?, args: List<String>, indices: IntRange, cursor: Int
        ): Pair<Node, Node?> {
            val nextCursor = cursor + 1
            if (nextCursor !in indices) return currentNode to parentNode
            val nextArg = args[nextCursor]
            val nextNode = currentNode[nextArg] ?: Node(
                nextArg
            ).also {
                currentNode[nextArg] = it
            }
            return buildNodeTree(nextNode, currentNode, args, indices, nextCursor)
        }

        private fun expandSetPattern(category: Category, aiml: Aiml): List<Category> {
            //Sorry gods of code optimization
            //I, honestly, don't know how to make it better
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

            data.map { aiml ->
                aiml.categories.filter {
                    it.that == null
                }.map {
                    expandSetPattern(it, aiml)
                }
            }.flatten().flatten().forEach {
                val args = it.pattern.split(" ")
                val pattern = args[0]
                val node = nodes[pattern] ?: Node(
                    pattern
                ).also { node ->
                    nodes[pattern] = node
                }
                val (currentNode, parentNode) = buildNodeTree(node, null, args, args.indices, 0)
                currentNode.copy(
                    template = it.template, commands = it.commands ?: emptyList()
                ).also { also ->
                    if (parentNode == null) {
                        nodes[pattern] = also
                    } else {
                        parentNode[also.pattern] = also
                    }
                }
            }

            return NodeManager(nodes, templatePostNodeProcessor, commandPostProcessor)
        }
    }
}