package base

import base.Node.Complete
import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor
import base.processor.command.CommandPostNodeProcessorImpl
import base.processor.template.TemplatePostNodeProcessorImpl
import base.processor.template.TemplatePostProcessor
import java.util.function.Supplier

class DefaultNodeManager constructor(
    private val nodes: HashMap<String, Node>,
    private val memory: Memory,
    private val templatePostNodeProcessor: NodeProcessor<TemplatePostProcessor.Result>,
    private val commandPostNodeProcessor: NodeProcessor<Unit>,
    private val history: ArrayList<Complete> = arrayListOf()
) : Supplier<DefaultNodeManager> {

    private fun String.isWildCard() = this == "*"

    override fun get(): DefaultNodeManager {
        val memory = memory.get()
        val templatePostNodeProcessor = TemplatePostNodeProcessorImpl(memory)
        val commandPostProcessor = CommandPostNodeProcessorImpl(memory)
        return DefaultNodeManager(nodes, memory, templatePostNodeProcessor, commandPostProcessor)
    }

    fun find(pattern: String): String {
        val args = pattern.split(" ")
        if (args.isEmpty()) throw IllegalStateException("A pattern must be provided")
        val stack = Stack()
        val node = internalFind(args, stack) ?: throw IllegalStateException("Partial matching not implemented yet")
        history.add(node)
        commandPostNodeProcessor(node, stack)
        return when (val result = templatePostNodeProcessor(node, stack)) {
            is TemplatePostProcessor.Result.Finish -> result.string
            is TemplatePostProcessor.Result.Rerun -> find(result.string)
            is TemplatePostProcessor.Result.Success -> throw IllegalStateException("Success result should be handled internally")
        }
    }

    private fun internalFind(args: List<String>, stack: Stack): Complete? {
        val indices = args.indices
        val arg = args[0]
        val last = history.lastOrNull()
        val node = if (last != null) {
            last.context[arg] ?: last.context["*"] ?: nodes[arg] ?: nodes["*"]
        } else {
            nodes[arg] ?: nodes["*"]
        } ?: return null
        return internalTailRecFind(node, arg, indices, nextOffset = 1, args, stack) as Complete?
    }

    private tailrec fun internalTailRecFind(
        node: Node,
        arg: String,
        indices: IntRange,
        nextOffset: Int,
        args: List<String>,
        stack: Stack
    ): Node? {
        if (node is Complete && node.index.isWildCard()) {
            return internalLookahead(
                node,
                arg,
                indices,
                nextOffset,
                args,
                stack
            )
        }
        if (nextOffset !in indices) {
            stack.pattern += arg
            return node
        }
        stack.pattern += arg
        val nextArg = args[nextOffset]
        val nextNode = node[nextArg] ?: node["*"] ?: return null
        return internalTailRecFind(nextNode, nextArg, indices, nextOffset = nextOffset + 1, args, stack)
    }

    data class LookaheadResult(
        val node: Node?,
        val arg: String?,
        val nextOffset: Int,
        val lookaheadArgs: ArrayList<String>
    )

    private fun internalLookahead(
        node: Node,
        arg: String,
        indices: IntRange,
        nextOffset: Int,
        args: List<String>,
        stack: Stack
    ): Node? {
        val (node, arg, nextOffset, lookaheadArgs) = internalTailRecLookahead(
            node,
            arg,
            indices,
            nextOffset,
            args,
            stack,
            lookaheadArgs = ArrayList(args.size)
        )
        val join = lookaheadArgs.joinToString(" ")
        stack.pattern += join
        stack.star += join
        if (nextOffset !in indices || arg == null) return node
        if (node == null) return null
        return internalTailRecFind(
            node,
            arg,
            indices,
            nextOffset = nextOffset,
            args,
            stack
        )
    }

    private tailrec fun internalTailRecLookahead(
        node: Node,
        arg: String,
        indices: IntRange,
        nextOffset: Int,
        args: List<String>,
        stack: Stack,
        lookaheadArgs: ArrayList<String>
    ): LookaheadResult {
        if (node !is Complete || !node.index.isWildCard()) return LookaheadResult(
            node,
            arg,
            nextOffset,
            lookaheadArgs
        )
        val next = node[arg] ?: node["*"]
        if (next == null) {
            lookaheadArgs += arg
            if (nextOffset !in indices) return LookaheadResult(
                node,
                null,
                nextOffset,
                lookaheadArgs
            )
            return internalTailRecLookahead(
                node,
                args[nextOffset],
                indices,
                nextOffset = nextOffset + 1,
                args,
                stack,
                lookaheadArgs
            )
        }
        return LookaheadResult(
            next,
            arg,
            nextOffset,
            lookaheadArgs
        )
    }
}