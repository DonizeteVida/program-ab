package base

import base.memory.Memory
import base.processor.command.CommandPostNodeProcessorImpl
import base.processor.template.TemplatePostNodeProcessorImpl
import base.regex.RegexPattern
import parser.json.Category
import parser.json.Knowledge

object DefaultNodeManagerBuilder {
    private fun buildNodeTree(
        nodeManager: NodeManager<KnowledgeNode>, category: Category
    ) {
        val args = category.pattern.split(" ")
        val indices = args.indices
        buildTailRecNodeTree(
            nodeManager, category, args, args[0], indices, nextOffset = 1
        )
    }

    private tailrec fun buildTailRecNodeTree(
        nodeManager: NodeManager<KnowledgeNode>,
        category: Category,
        args: List<String>,
        arg: String,
        indices: IntRange,
        nextOffset: Int
    ) {
        if (nextOffset !in indices) {
            KnowledgeNode(
                pattern = arg, template = category.template, commands = category.commands ?: emptyList()
            ).also {
                nodeManager[arg]?.apply(it::plusAssign)
                nodeManager[arg] = it
            }
            return
        }
        val nextNodeManager: NodeManager<KnowledgeNode> = nodeManager[arg] ?: KnowledgeNode(
            arg, ""
        ).also {
            nodeManager[arg]?.apply(it::plusAssign)
            nodeManager[arg] = it
        }
        return buildTailRecNodeTree(
            nextNodeManager, category, args, arg = args[nextOffset], indices, nextOffset = nextOffset + 1
        )
    }

    private fun expandSetPattern(category: Category, knowledge: Knowledge): List<Category> {
        //Sorry gods of code optimization
        //I, honestly, don't know how to make it better
        val pattern = category.pattern
        val matches = RegexPattern.SET.findAll(pattern)
        if (!matches.any()) return arrayListOf(category)
        val expanded = arrayListOf(pattern)
        for (m in matches) {
            val all = m.groups[0]!!.value
            val variable = m.groups[1]!!.value
            val sets = knowledge.sets[variable] ?: throw IllegalStateException("Set $variable not found")
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

    //it only exists for polymorphism shit
    private data class NodesNodeManager(
        private val nodes: HashMap<String, KnowledgeNode>
    ) : NodeManager<KnowledgeNode> {
        override fun get(index: String) = nodes[index]

        override fun plusAssign(other: KnowledgeNode) =
            throw IllegalStateException("Problem in Nodes graph construction")

        override fun set(index: String, node: KnowledgeNode) {
            nodes[index] = node
        }
    }

    fun build(knowledges: List<Knowledge>): DefaultNodeManager {
        val contextual = hashMapOf<String, KnowledgeNode>()
        val nodes = hashMapOf<String, KnowledgeNode>()
        val nodesManager = NodesNodeManager(nodes)
        val memory = Memory()
        val templatePostNodeProcessor = TemplatePostNodeProcessorImpl(memory)
        val commandPostProcessor = CommandPostNodeProcessorImpl(memory)

        knowledges.map(Knowledge::variables).forEach(memory.initial::putAll)

        knowledges.map { knowledge ->
            knowledge.categories.filter {
                it.context == null
            }.map { category -> category to knowledge }
        }.flatten().map { (category, knowledge) -> expandSetPattern(category, knowledge) }.flatten().forEach {
            buildNodeTree(nodesManager, it)
        }

        knowledges.map { knowledge ->
            knowledge.categories.filter {
                it.context != null
            }.map { category -> category to knowledge }
        }.flatten().map { (category, knowledge) -> expandSetPattern(category, knowledge) }.flatten().forEach {
            buildNodeTree(nodesManager, it)
        }

        return DefaultNodeManager(nodes, memory, templatePostNodeProcessor, commandPostProcessor)
    }
}