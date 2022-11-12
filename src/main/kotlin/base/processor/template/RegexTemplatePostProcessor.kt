package base.processor.template

import base.processor.NodeProcessor
import base.processor.RegexProcessorImpl
import base.regex.RegexPattern

abstract class RegexTemplatePostProcessor(
    regexPattern: RegexPattern
) : RegexProcessorImpl<NodeProcessor.Action>(
    regexPattern
) {
    override fun onFinish(builder: StringBuilder, matchCounter: Int): NodeProcessor.Action =
        NodeProcessor.Action.Success(builder.toString())
}