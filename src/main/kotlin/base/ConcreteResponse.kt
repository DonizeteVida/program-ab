package base

data class ConcreteResponse(
    val response: String
): Response {
    override fun invoke(stack: Stack): String {
        return response.replace("{{ star }}", stack.template[0])
    }
}