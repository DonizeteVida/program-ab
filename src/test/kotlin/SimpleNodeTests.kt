import base.memory.Stack
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleNodeTests : BaseNodeTests("simple") {

    @Test
    fun `should find a node for an empty input`() {
        val input = ""

        val expectedResponse = "What do you expect I should response ?"
        val expectedStack = Stack(
            pattern = arrayListOf("")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should be able to find a simple node`() {
        val input = "Hello"

        val expectedResponse = "Hello there"
        val expectedStack = Stack(
            pattern = arrayListOf("Hello")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should be able to find a 'eraser' node`() {
        val input = "Hello there"

        val expectedResponse = "Hello"
        val expectedStack = Stack(
            pattern = arrayListOf("Hello", "there")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should throw an exception for a not found node`() {
        val input = "What ever"
        val exception = assertThrows<IllegalStateException> {
            defaultNodeManager.find(input)
        }
        assertEquals("Not found a satisfiable node for: What ever", exception.message)
    }
}