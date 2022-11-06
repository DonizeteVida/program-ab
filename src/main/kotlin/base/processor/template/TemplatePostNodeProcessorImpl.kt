package base.processor.template

import base.KnowledgeNode
import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor

class TemplatePostNodeProcessorImpl(
    private val memory: Memory
) : NodeProcessor<TemplatePostProcessor.Result> {
    private val transformers = arrayOf(
        ::StarRegexTemplatePostProcessor,
        ::PatternRegexTemplatePostProcessor,
        ::GetFallbackRegexTemplatePostProcessor,
        ::GetRegexTemplatePostProcessor,
        ::SraiRegexTemplatePostProcessor
    )
    private val indices = transformers.indices

    override operator fun invoke(node: KnowledgeNode, stack: Stack): TemplatePostProcessor.Result {
        val builder = StringBuilder(node.template)
        return internalTransform(builder, 0, stack)
    }

    private fun internalTransform(builder: StringBuilder, index: Int, stack: Stack): TemplatePostProcessor.Result =
        when (val result = transformers[index](stack, memory)(builder)) {
            is TemplatePostProcessor.Result.Rerun -> result
            is TemplatePostProcessor.Result.Success -> {
                if (index + 1 !in indices) {
                    TemplatePostProcessor.Result.Finish(builder.toString())
                } else {
                    internalTransform(builder, index + 1, stack)
                }
            }

            else -> throw IllegalStateException("Internal result not properly handled: $result")
        }
}