package base.processor.command

import base.Node
import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor

class CommandPostNodeProcessorImpl(
    private val memory: Memory
) : NodeProcessor<Unit> {
    override fun invoke(node: Node, stack: Stack) {

    }
}