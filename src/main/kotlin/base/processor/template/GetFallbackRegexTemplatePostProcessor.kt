package base.processor.template

import base.memory.Memory
import base.memory.Stack
import base.regex.RegexPattern

class GetFallbackRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.GET_FALLBACK
) {
    override fun onMatch(matchResult: MatchResult): String {
        val variable1 = matchResult.groups[1]!!.value
        val variable2 = matchResult.groups[2]!!.value
        return memory[variable1] ?: memory[variable2]
        ?: throw IllegalStateException("Variable $variable1 neither $variable2 weren't found in $memory")
    }
}