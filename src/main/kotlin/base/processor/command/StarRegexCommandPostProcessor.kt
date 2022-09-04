package base.processor.command

import base.memory.Memory
import base.memory.Stack
import base.regex.RegexPattern

class StarRegexCommandPostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexCommandPostProcessor(
    RegexPattern.STAR
) {
    override fun onMatch(matchResult: MatchResult): String {
        val group = matchResult.groups[1] ?: throw IllegalStateException("Should never happen")
        val string = group.value
        val integer = string.toInt()
        return stack.star[integer]
    }
}