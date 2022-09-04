package base.processor

interface RegexProcessor<T> {
    operator fun invoke(builder: StringBuilder): T
    fun onMatch(matchResult: MatchResult): String
    fun onFinish(builder: StringBuilder, matchCounter: Int): T
}