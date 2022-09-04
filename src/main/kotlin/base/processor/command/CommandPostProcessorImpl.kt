package base.processor.command

import base.Node
import base.memory.Memory
import base.memory.Stack
import base.processor.Processor

class CommandPostProcessorImpl(
    private val memory: Memory
) : Processor<Unit> {
    override fun invoke(node: Node, stack: Stack) {

    }
}