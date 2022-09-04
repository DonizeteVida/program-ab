package base.processor.template

import base.memory.Memory
import base.regex.RegexPattern
import base.memory.Stack

class GetRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.GET
) {
    override fun onMatch(matchResult: MatchResult): String {
        val group = matchResult.groups[1] ?: throw IllegalStateException("Should never happen")
        val name = group.value
        return memory.variables[name] ?: throw IllegalStateException("Variable $name not found in $memory")
    }
}