package base.processor

import base.Node
import base.memory.Stack

interface NodeProcessor<T : Any> {
    operator fun invoke(node: Node.Complete, stack: Stack): T
}