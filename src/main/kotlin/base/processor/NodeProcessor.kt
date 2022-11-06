package base.processor

import base.KnowledgeNode
import base.memory.Stack

interface NodeProcessor<T : Any> {
    operator fun invoke(node: KnowledgeNode, stack: Stack): T
}