package base.processor.template

import base.processor.RegexProcessorImpl
import base.regex.RegexPattern
import base.processor.template.TemplatePostProcessor.Result

abstract class RegexTemplatePostProcessor(
    regexPattern: RegexPattern
) : RegexProcessorImpl<Result>(
    regexPattern
) {
    override fun onFinish(builder: StringBuilder, matchCounter: Int): Result = Result.Success
}