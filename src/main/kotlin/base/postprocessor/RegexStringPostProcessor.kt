package base.postprocessor

import base.RegexPattern

abstract class RegexStringPostProcessor(
    private val regexPattern: RegexPattern
) : StringPostProcessor {
    override fun invoke(builder: StringBuilder): StringPostProcessor.Result {
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

    open fun onFinish(string: StringBuilder, matchCounter: Int): StringPostProcessor.Result =
        StringPostProcessor.Result.Success
}