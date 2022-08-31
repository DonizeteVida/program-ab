package base.transformation

import base.Memory
import base.RegexPattern
import base.Stack

class PatternRegexStringPostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexStringPostProcessor(
    RegexPattern.PATTERN
) {
    override fun onMatch(matchResult: MatchResult): String {
        val group = matchResult.groups[1] ?: throw IllegalStateException("Should never happen")
        val string = group.value
        val integer = string.toInt()
        return stack.pattern[integer]
    }
}