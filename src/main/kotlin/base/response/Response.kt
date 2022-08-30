package base.response

import base.DynamicRegexPattern
import base.Memory
import base.Stack

sealed interface Response : (String, Stack, Memory) -> String {
    companion object {
        fun transform(template: String, stack: Stack, memory: Memory): String {
            val stargized = Star(template, stack, memory)
            val getized = Get(stargized, stack, memory)
            val assignized = Assign(getized, stack, memory)
            return assignized
        }
    }

    object Get : Response {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = DynamicRegexPattern.GET.regex
            val matches = regex.findAll(template)

            for (match in matches) {
                val all = match.groups[0] ?: continue
                val size = all.range.last - all.range.first

                val name = match.groups[1]?.value ?: continue
                val value = memory.variables[name]!!

                strBuilder.replace(
                    all.range.first + offset,
                    all.range.last + offset + 1,
                    value
                )

                offset += value.length - size - 1
            }

            return strBuilder.toString()
        }
    }

    object Assign : Response {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = DynamicRegexPattern.ASSIGN.regex
            val matches = regex.findAll(template)

            for (match in matches) {
                val all = match.groups[0] ?: continue
                val size = all.range.last - all.range.first

                val name = match.groups[1]?.value ?: continue
                val value = match.groups[2]?.value ?: continue

                memory.variables[name] = value

                strBuilder.replace(
                    all.range.first + offset,
                    all.range.last + offset + 1,
                    value
                )

                offset += value.length - size - 1
            }

            return strBuilder.toString()
        }
    }

    object Star : Response {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = DynamicRegexPattern.STAR.regex
            val matches = regex.findAll(template)
            for (match in matches) {
                val all = match.groups[0] ?: continue
                val group = match.groups[1] ?: continue
                val size = all.range.last - all.range.first
                val value = group.value
                val index = value.toInt()

                val replace = stack.template[index]

                strBuilder.replace(
                    all.range.first + offset,
                    all.range.last + offset + 1,
                    replace
                )

                offset += replace.length - size - 1
            }
            return strBuilder.toString()
        }
    }
}