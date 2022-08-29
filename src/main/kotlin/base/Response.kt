package base

interface Response {
    operator fun invoke(stack: Stack): String
}