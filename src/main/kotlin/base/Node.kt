package base

interface Node<T : Node<T>> {
    operator fun get(index: String): T?
    operator fun set(index: String, node: T)
    operator fun plusAssign(other: T)
}