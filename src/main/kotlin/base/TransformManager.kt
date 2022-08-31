package base

import base.postprocessor.*

class TransformManager(
    private val memory: Memory
) {
    private val transformers = arrayOf<(Stack, Memory) -> StringPostProcessor>(
        ::StarRegexStringPostProcessor,
        ::PatternRegexStringPostProcessor,
        ::GetFallbackRegexStringPostProcessor,
        ::GetRegexStringPostProcessor,
        ::SraiRegexStringPostProcessor
    )
    private val indices = transformers.indices

    fun transform(node: Node, stack: Stack): StringPostProcessor.Result {
        val builder = StringBuilder(node.template)
        return internalTransform(builder, 0, stack)
    }

    private fun internalTransform(builder: StringBuilder, index: Int, stack: Stack): StringPostProcessor.Result =
        when (val result = transformers[index](stack, memory)(builder)) {
            is StringPostProcessor.Result.Rerun -> result
            is StringPostProcessor.Result.Success -> {
                if (index + 1 !in indices) {
                    StringPostProcessor.Result.Finish(builder.toString())
                } else {
                    internalTransform(builder, index + 1, stack)
                }
            }

            else -> throw IllegalStateException("Internal result not properly handled: $result")
        }
}