package base.processor

import base.regex.RegexPattern

abstract class RegexProcessorImpl<T>(
    private val regexPattern: RegexPattern
) : RegexProcessor<T> {
    override fun invoke(builder: StringBuilder): T {
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
}