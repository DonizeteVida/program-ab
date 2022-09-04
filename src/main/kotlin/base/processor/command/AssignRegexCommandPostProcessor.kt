package base.processor.command

import base.memory.Memory
import base.memory.Stack
import base.regex.RegexPattern

class AssignRegexCommandPostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexCommandPostProcessor(
    RegexPattern.ASSIGN
) {
    override fun onMatch(matchResult: MatchResult): String {
        val variable = matchResult.groups[1]!!.value
        val value = matchResult.groups[2]!!.value
        memory.variables[variable] = value
        return ""
    }
}