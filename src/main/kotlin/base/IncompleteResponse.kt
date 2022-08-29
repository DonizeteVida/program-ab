package base

object IncompleteResponse : Response {
    override fun invoke() = throw IllegalStateException("Should never happen")
}