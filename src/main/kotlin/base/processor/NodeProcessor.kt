package base.processor

import base.DefaultNodeManager
import base.Node
import base.memory.Memory
import base.memory.Stack

interface NodeProcessor {
    sealed interface Action {
        operator fun invoke(defaultNodeManager: DefaultNodeManager): String
        data class Success(
            val result: String
        ) : Action {
            override fun invoke(defaultNodeManager: DefaultNodeManager) = result
        }
        data class ReRun(
            val result: String
        ) : Action {
            override fun invoke(defaultNodeManager: DefaultNodeManager) = defaultNodeManager.find(result)
        }
        object None : Action {
            override fun invoke(defaultNodeManager: DefaultNodeManager) = throw IllegalStateException("None NodeProcessor Action should never be used")
        }
    }
    operator fun invoke(node: Node.Complete, stack: Stack, memory: Memory): Action
}