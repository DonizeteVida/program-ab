package base

interface NodeManager<T : Node<*>> {
    operator fun get(index: String): T?
    operator fun set(index: String, node: T)
    operator fun plusAssign(other: T)
}