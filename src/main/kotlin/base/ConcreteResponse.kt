package base

data class ConcreteResponse(
    val response: String
): Response {
    override fun invoke(stack: Stack): String {
        var offset = 0
        val strBuilder = StringBuilder(response)
        val pattern = DynamicRegexPattern.STAR.pattern
        val matches = pattern.findAll(response)
        for (match in matches) {
            val all = match.groups[0] ?: continue
            val group = match.groups[1] ?: continue
            val size = all.range.last - all.range.first
            val value = group.value
            val index = value.toInt()

            val replace = stack.template[index]

            strBuilder.replace(
                all.range.first + offset,
                all.range.last + offset + 1,
                replace
            )

            offset += replace.length - size - 1
        }
        return strBuilder.toString()
    }
}