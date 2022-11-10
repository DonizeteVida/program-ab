package base.processor.command

import base.Node
import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor

class CommandPostNodeProcessorImpl(
    private val memory: Memory
) : NodeProcessor<Unit> {
    private val processors = arrayOf(
        ::StarRegexCommandPostProcessor,
        ::AssignRegexCommandPostProcessor
    )

    override fun invoke(node: Node.Complete, stack: Stack) {
        for (command in node.commands) {
            val builder = StringBuilder(command)
            processors.map {
                it(stack, memory)
            }.forEach {
                it(builder)
            }
        }
    }
}