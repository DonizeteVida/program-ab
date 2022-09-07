package base.processor.template

import base.memory.Memory
import base.memory.Stack
import base.regex.RegexPattern

class GetRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.GET
) {
    override fun onMatch(matchResult: MatchResult): String {
        val name = matchResult.groups[1]!!.value
        return memory.variables[name] ?: throw IllegalStateException("Variable $name not found in $memory")
    }
}