package base.transformation

import base.RegexPattern

abstract class RegexStringPostProcessor(
    private val regexPattern: RegexPattern
) : StringPostProcessor {
    override fun invoke(builder: StringBuilder): StringPostProcessor.Result {
        var offset = 0
        val matches = regexPattern.findAll(builder)
        for (match in matches) {
            val entireMatch = match.groups[0] ?: continue
            val range = entireMatch.range
            val size = range.last - range.first - 1
            val result = onMatch(match)
            builder.replace(
                range.first + offset,
                range.last + offset + 1,
                result
            )
            offset += size - result.length - 1
        }
        return onFinish(builder, matches.count())
    }

    abstract fun onMatch(matchResult: MatchResult): String

    open fun onFinish(string: StringBuilder, matchCounter: Int): StringPostProcessor.Result = StringPostProcessor.Result.Success
}