package base.transformation

import base.Memory
import base.RegexPattern
import base.Stack

class SraiRegexStringPostProcessor(
    private val stack: Stack,
    private val memory: Memory
) : RegexStringPostProcessor(
    RegexPattern.SRAI
) {
    override fun onMatch(matchResult: MatchResult) =
        matchResult.groups[1]?.value ?: throw IllegalStateException("Should never happen")

    override fun onFinish(string: StringBuilder, matchCounter: Int) =
        if (matchCounter > 0) {
            StringPostProcessor.Result.Rerun(string.toString())
        } else {
            StringPostProcessor.Result.Success
        }
}