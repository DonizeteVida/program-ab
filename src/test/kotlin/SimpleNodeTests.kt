import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleNodeTests : BaseNodeTests("simple") {

    @Test
    fun `should be able to find a simple node`() {
        val input = "Hello"
        val expected = "Hello there"
        assertEquals(expected, defaultNodeManager.find(input))
    }

    @Test
    fun `should be able to find a 'eraser' node`() {
        val input = "Hello there"
        val expected = "Hello"
        assertEquals(expected, defaultNodeManager.find(input))
    }
}