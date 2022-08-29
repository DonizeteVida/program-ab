package base

data class ConcreteResponse(
    val response: String
): Response {
    override fun invoke(): String = response
}