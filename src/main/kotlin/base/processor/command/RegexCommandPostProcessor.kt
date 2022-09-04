package base.processor.command

import base.processor.RegexProcessorImpl
import base.regex.RegexPattern
import base.processor.template.TemplatePostProcessor

abstract class RegexCommandPostProcessor(
    regexPattern: RegexPattern
) : RegexProcessorImpl<Unit>(regexPattern) {

}