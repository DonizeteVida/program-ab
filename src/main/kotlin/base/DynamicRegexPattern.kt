package base

enum class DynamicRegexPattern(
    val regex: Regex
) {
    STAR("""\{\{\s*star:(\d+)\s*}}""".toRegex()),
    ASSIGN("""\{\{\s*(\w+)=(\w+)\s*}}""".toRegex()),
    GET("""\{\{\s*(\w+)\s*}}""".toRegex()),
    SRAI("""\{\{\s*srai:\s*(.+)\s*}}""".toRegex())
}