package base.response

import base.RegexPattern
import base.Memory
import base.NodeManager
import base.Stack

sealed interface Response<T> : (String, Stack, Memory) -> T {
    companion object {
        fun transform(
            template: String,
            stack: Stack,
            memory: Memory,
            nodeManager: NodeManager
        ): String {
            val stargized = Star(template, stack, memory)
            val fallbackzed = GetFallback(stargized, stack, memory)
            val getized = Get(fallbackzed, stack, memory)
            val assignized = Assign(getized, stack, memory)
            val (sraized, isSraized) = Srai(assignized, stack, memory)
            if (isSraized) return nodeManager.find(sraized)
            return sraized
        }
    }

    object Srai : Response<Pair<String, Boolean>> {
        override fun invoke(template: String, stack: Stack, memory: Memory): Pair<String, Boolean> {
            val strBuilder = StringBuilder(template)
            val regex = RegexPattern.SRAI.regex
            val matches = regex.findAll(template).toList()
            if (matches.isEmpty()) return template to false
            val match = matches.single()

            val all = match.groups[0] ?: return template to false
            val value = match.groups[1]?.value ?: return template to false

            strBuilder.replace(
                all.range.first,
                all.range.last + 1,
                value
            )

            return strBuilder.toString() to true
        }
    }

    object GetFallback : Response<String> {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = RegexPattern.GET_FALLBACK.regex
            val matches = regex.findAll(template)

            for (match in matches) {
                val all = match.groups[0] ?: continue
                val size = all.range.last - all.range.first

                val name = match.groups[1]?.value ?: continue
                val fallback = match.groups[2]?.value ?: continue
                val value = memory.variables[name] ?: fallback

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

    object Get : Response<String> {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = RegexPattern.GET.regex
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

    object Assign : Response<String> {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = RegexPattern.ASSIGN.regex
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

    object Star : Response<String> {
        override fun invoke(template: String, stack: Stack, memory: Memory): String {
            var offset = 0
            val strBuilder = StringBuilder(template)
            val regex = RegexPattern.STAR.regex
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