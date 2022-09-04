package base.processor.template

import base.RegexPattern

abstract class RegexTemplatePostProcessor(
    private val regexPattern: RegexPattern
) : TemplatePostProcessor {
    override fun invoke(builder: StringBuilder): TemplatePostProcessor.Result {
        var offset = 0
        val matches = regexPattern.findAll(builder.toString())
        val count = matches.count()
        for (m in matches) {
            val match = m.groups[0] ?: continue
            val range = match.range
            val result = onMatch(m)
            builder.replace(
                range.first + offset,
                range.last + offset + 1,
                result
            )
            offset +=  result.length - match.value.length
        }
        return onFinish(builder, count)
    }

    abstract fun onMatch(matchResult: MatchResult): String

    open fun onFinish(string: StringBuilder, matchCounter: Int): TemplatePostProcessor.Result =
        TemplatePostProcessor.Result.Success
}