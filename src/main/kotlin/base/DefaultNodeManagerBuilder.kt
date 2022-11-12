package base

import base.Node.Complete
import base.memory.Memory
import base.regex.RegexPattern
import parser.json.Category
import parser.json.Knowledge

object DefaultNodeManagerBuilder {
    private fun buildNodeTree(
        node: Node,
        category: Category
    ): Node {
        val args = category.pattern.split(" ")
        val indices = args.indices
        return buildTailRecNodeTree(
            node,
            category,
            args,
            args[0],
            indices,
            nextOffset = 1
        )
    }

    private tailrec fun buildTailRecNodeTree(
        node: Node,
        category: Category,
        args: List<String>,
        arg: String,
        indices: IntRange,
        nextOffset: Int
    ): Node = if (nextOffset !in indices) Complete(
        index = arg,
        template = category.template,
        commands = category.commands ?: emptyList()
    ).also {
        node[arg]?.apply(it::plusAssign)
        node[arg] = it
    } else {
        val node = node[arg] ?: Node.Incomplete(arg).also {
            node[arg]?.apply(it::plusAssign)
            node[arg] = it
        }
        buildTailRecNodeTree(
            node,
            category,
            args,
            arg = args[nextOffset],
            indices,
            nextOffset = nextOffset + 1
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
    private data class NodesNode(
        override val nodes: HashMap<String, Node>,
        override val index: String = ""
    ) : Node {
        override fun get(index: String) = nodes[index]

        override fun plusAssign(other: Node) {
            nodes[other.index]?.apply(other::plusAssign)
            nodes[other.index] = other
        }

        override fun set(index: String, node: Node) {
            nodes[index] = node
        }
    }

    fun build(knowledges: List<Knowledge>): DefaultNodeManager {
        val contextual = hashMapOf<String, Node>()
        val nodes = hashMapOf<String, Node>()
        val nodesManager = NodesNode(nodes)
        val memory = Memory()

        knowledges.map(Knowledge::variables).forEach(memory.initial::putAll)

        knowledges.map { knowledge ->
            knowledge.categories.filter {
                it.context == null
            }.map { category -> category to knowledge }
        }.flatten().map { (category, knowledge) -> expandSetPattern(category, knowledge) }.flatten().forEach {
            val node = buildNodeTree(nodesManager, it)
            contextual[it.template + it.id] = node
        }

        knowledges.map { knowledge ->
            knowledge.categories.filter {
                it.context != null
            }.map { category -> category to knowledge }
        }.flatten().map { (category, knowledge) -> expandSetPattern(category, knowledge) }.flatten().forEach {
            val context = it.context ?: throw IllegalStateException("Context is null")
            val node = contextual[context.template + context.id] as? Complete
                ?: throw IllegalStateException("Contextual parent node not found")
            val newNode = buildNodeTree(NodesNode(node.context), it)
            contextual[it.template + it.id] = newNode
        }

        contextual.clear()

        return DefaultNodeManager(
            nodes,
            memory
        )
    }
}