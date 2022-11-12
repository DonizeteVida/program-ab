package base.processor.template

import base.memory.Memory
import base.memory.Stack
import base.processor.NodeProcessor
import base.regex.RegexPattern

class SraiRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.SRAI
) {
    override fun onMatch(matchResult: MatchResult) = matchResult.groups[1]!!.value

    override fun onFinish(builder: StringBuilder, matchCounter: Int) =
        if (matchCounter > 0) {
            NodeProcessor.Action.ReRun(builder.toString())
        } else {
            NodeProcessor.Action.Success(builder.toString())
        }
}