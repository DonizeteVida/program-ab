package base.processor.template

import base.memory.Memory
import base.regex.RegexPattern
import base.memory.Stack

class StarRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.STAR
) {
    override fun onMatch(matchResult: MatchResult): String {
        val group = matchResult.groups[1] ?: throw IllegalStateException("Should never happen")
        val string = group.value
        val integer = string.toInt()
        return stack.star[integer]
    }
}