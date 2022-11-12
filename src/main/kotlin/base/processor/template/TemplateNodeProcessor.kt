package base.processor.template

import base.Node
import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor

object TemplateNodeProcessor : NodeProcessor {
    private val transformers = arrayOf(
        ::StarRegexTemplatePostProcessor,
        ::PatternRegexTemplatePostProcessor,
        ::GetFallbackRegexTemplatePostProcessor,
        ::GetRegexTemplatePostProcessor,
        ::SraiRegexTemplatePostProcessor
    )
    private val indices = transformers.indices

    override operator fun invoke(node: Node.Complete, stack: Stack, memory: Memory): NodeProcessor.Action {
        val builder = StringBuilder(node.template)
        return internalTransform(builder, 0, stack, memory)
    }

    private tailrec fun internalTransform(
        builder: StringBuilder,
        index: Int,
        stack: Stack,
        memory: Memory
    ): NodeProcessor.Action =
        when (val result = transformers[index](stack, memory)(builder)) {
            is NodeProcessor.Action.ReRun -> result
            is NodeProcessor.Action.Success -> {
                if (index + 1 !in indices) {
                    NodeProcessor.Action.Success(builder.toString())
                } else {
                    internalTransform(builder, index + 1, stack, memory)
                }
            }

            else -> throw IllegalStateException("Internal result not properly handled: $result")
        }
}