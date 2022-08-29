package base

enum class DynamicRegexPattern(
    val pattern: Regex
) {
    STAR("""\{\{\s*star:(\d+)\s*}}""".toRegex()),
    ASSIGN("""\{\{\s*(\w+)=(\w+)\s*}}""".toRegex())
}