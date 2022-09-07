package base.processor.template

import base.memory.Memory
import base.memory.Stack
import base.regex.RegexPattern

class PatternRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.PATTERN
) {
    override fun onMatch(matchResult: MatchResult): String {
        return matchResult.groups[1]!!.value.toInt().let(stack.pattern::get)
    }
}