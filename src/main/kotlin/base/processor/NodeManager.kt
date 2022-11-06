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
import java.util.function.Supplier

class NodeManager private constructor(
    private val nodes: HashMap<String, Node> = hashMapOf(),
    private val templatePostNodeProcessor: NodeProcessor<TemplatePostProcessor.Result>,
    private val commandPostNodeProcessor: NodeProcessor<Unit>
) : Supplier<NodeManager> {
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
        val indices = args.indices

        var cursor = 0
        var last: Node? = null

        while (cursor in indices) {
            val arg = args[cursor++]
            val current = last?.get(arg) ?: last?.get("*") ?: nodes[arg] ?: nodes["*"] ?: return null
            last = current
            if (current.isWildCard) {
                //lookahead logic
                val matches = arrayListOf(arg)
                while (cursor in indices) {
                    val lookahead = args[cursor++]
                    val node = current[lookahead] ?: current["*"]
                    if (node != null) {
                        last = node
                        break
                    }
                    matches += lookahead
                }
                val match = matches.joinToString(" ")
                stack.star += match
                stack.pattern += match
            } else {
                stack.pattern += arg
            }
        }
        return last
    }

    companion object {
        private fun buildNodeTree(
            nodes: MutableMap<String, Node>,
            category: Category
        ) {
            val args = category.pattern.split(" ")
            val indices = args.indices

            var cursor = 0
            var arg = args[cursor]
            var prev = nodes[arg]?: Node(arg, "").also {
                nodes[arg] = it
            }
            while (++cursor in indices) {
                arg = args[cursor]
                prev = prev[arg] ?: Node(
                    arg,
                    if (cursor + 1 in indices) "" else category.template
                ).also {
                    prev[arg] = it
                }
            }
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

        fun findThatNode(that: String, thats: List<String>, indices: IntRange, cursor: Int): Node? {

            return null
        }

        fun build(data: List<Aiml>): NodeManager {
            val thats = hashMapOf<String, Node>()
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
                buildNodeTree(nodes, it)
            }

            data.map { aiml ->
                aiml.categories.filter {
                    it.that != null
                }.forEach {
                    val thats = it.that?.split(" ") ?: emptyList()
                    val that = thats[0]
                    val thatNode = findThatNode(that, thats, thats.indices, 0)
                    if (thatNode != null) {

                    }
                }
            }

            return NodeManager(nodes, templatePostNodeProcessor, commandPostProcessor)
        }
    }

    override fun get(): NodeManager {
        val memory = Memory()
        val templatePostNodeProcessor = TemplatePostNodeProcessorImpl(memory)
        val commandPostProcessor = CommandPostNodeProcessorImpl(memory)
        return NodeManager(nodes, templatePostNodeProcessor, commandPostProcessor)
    }
}