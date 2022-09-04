package base.processor.command

import base.processor.RegexProcessorImpl
import base.regex.RegexPattern

abstract class RegexCommandPostProcessor(
    regexPattern: RegexPattern
) : RegexProcessorImpl<Unit>(regexPattern) {
    override fun onFinish(builder: StringBuilder, matchCounter: Int) = Unit
}