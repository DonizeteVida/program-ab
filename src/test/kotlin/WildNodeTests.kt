import base.memory.Stack
import kotlin.test.Test
import kotlin.test.assertEquals

class WildNodeTests : BaseNodeTests("wild") {

    @Test
    fun `should be able to find a generic node`() {
        val input = "Hello there"

        val expectedResponse = "A default response will be provided"
        val expectedStack = Stack(
            pattern = arrayListOf("Hello there"),
            star = arrayListOf("Hello there")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should be able to find a double wild node`() {
        val input = "death or glory ?"

        val expectedResponse = "Maybe glory. But death could be great too"
        val expectedStack = Stack(
            pattern = arrayListOf("death", "or", "glory", "?"),
            star = arrayListOf("death", "glory")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should be able to lookahead and fit wild properly`() {
        val input = "To be or not to be"

        val expectedResponse = "Why not not to be? To be seems to be crazy"
        val expectedStack = Stack(
            pattern = arrayListOf("To be", "or", "not to be"),
            star = arrayListOf("To be", "not to be")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }
}