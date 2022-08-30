package base

enum class DynamicRegexPattern(
    val regex: Regex
) {
    STAR("""\{\{\s*star:(\d+)\s*}}""".toRegex()),
    ASSIGN("""\{\{\s*(\w+)=(\w+)\s*}}""".toRegex()),
    GET("""\{\{\s*(\w+)\s*}}""".toRegex()),
    /**
     * pre-built SRAI function
     */
    SRAI("""\{\{\s*srai\((.+)\)\s*}}""".toRegex())
}