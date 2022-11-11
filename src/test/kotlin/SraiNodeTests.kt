import base.memory.Stack
import kotlin.test.Test
import kotlin.test.assertEquals

class SraiNodeTests : BaseNodeTests("srai") {

    @Test
    fun `should be able to find a generic node`() {
        val input = "cat or dog"

        val expectedResponse = "Probably not. But a dog could be a cat."
        val expectedStack = Stack(
            pattern = arrayListOf("can", "a", "cat", "be", "a", "dog", "?"),
            star = arrayListOf("cat", "dog")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }
}