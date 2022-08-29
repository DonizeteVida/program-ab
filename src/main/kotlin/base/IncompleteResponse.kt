package base

object IncompleteResponse : Response {
    override fun invoke(stack: Stack) = throw IllegalStateException("Should never happen")
}