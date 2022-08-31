package base.transformation

interface StringPostProcessor {
    sealed interface Result {
        data class Finish(
            val string: String
        ) : Result

        data class Rerun(
            val string: String
        ) : Result

        object Success : Result
    }

    operator fun invoke(builder: StringBuilder): Result
}