package base.regex

enum class RegexPattern(
    private val regex: Regex
) {
    STAR("""\{\{\s*star:(\d+)\s*}}""".toRegex()),
    ASSIGN("""\{\{\s*(\w+)=(.+)\s*}}""".toRegex()),
    GET("""\{\{\s*(\w+)\s*}}""".toRegex()),
    GET_FALLBACK("""\{\{\s*(\w+)\s*\?:\s*(\w+)\s*}}""".toRegex()),
    SRAI("""\{\{\s*srai\s*\((.+)\)\s*}}""".toRegex()),
    SET("""\{\{\s*set:(\w+)\s*}}""".toRegex()),
    PATTERN("""\{\{\s*pattern:(\d+)\s*}}""".toRegex());

    fun findAll(char: CharSequence) = regex.findAll(char)
}