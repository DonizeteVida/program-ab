package base.processor.template

import base.memory.Memory
import base.RegexPattern
import base.memory.Stack

class GetFallbackRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.GET_FALLBACK
) {
    override fun onMatch(matchResult: MatchResult): String {
        val group1 = matchResult.groups[1] ?: throw IllegalStateException("Should never happen")
        val group2 = matchResult.groups[2] ?: throw IllegalStateException("Should never happen")
        val name1 = group1.value
        val name2 = group2.value
        return memory.variables[name1] ?: memory.variables[name2]
        ?: throw IllegalStateException("Variable $name1 neither $name2 not found in $memory")
    }
}