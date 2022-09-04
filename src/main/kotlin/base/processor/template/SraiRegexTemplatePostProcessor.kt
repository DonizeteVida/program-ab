package base.processor.template

import base.memory.Memory
import base.regex.RegexPattern
import base.memory.Stack

class SraiRegexTemplatePostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexTemplatePostProcessor(
    RegexPattern.SRAI
) {
    override fun onMatch(matchResult: MatchResult) =
        matchResult.groups[1]?.value ?: throw IllegalStateException("Should never happen")

    override fun onFinish(builder: StringBuilder, matchCounter: Int) =
        if (matchCounter > 0) {
            TemplatePostProcessor.Result.Rerun(builder.toString())
        } else {
            TemplatePostProcessor.Result.Success
        }
}