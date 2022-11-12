package base.processor.command

import base.Node
import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor

object CommandNodeProcessor : NodeProcessor {
    private val processors = arrayOf(
        ::StarRegexCommandPostProcessor,
        ::AssignRegexCommandPostProcessor
    )

    override fun invoke(node: Node.Complete, stack: Stack, memory: Memory): NodeProcessor.Action {
        for (command in node.commands) {
            val builder = StringBuilder(command)
            processors.map {
                it(stack, memory)
            }.forEach {
                it(builder)
            }
        }
        return NodeProcessor.Action.None
    }
}