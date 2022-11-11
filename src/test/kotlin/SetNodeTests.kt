import base.memory.Stack
import kotlin.test.Test
import kotlin.test.assertEquals

class SetNodeTests : BaseNodeTests("set") {

    @Test
    fun `should be able to expand a simple set pattern`() {
        val sets = arrayListOf(
            "happy",
            "sad",
            "boring"
        )

        for (set in sets) {
            val input = "are you $set ?"

            val expectedResponse = "No. I'm not $set. I'm a robot."
            val expectedStack = Stack(
                pattern = arrayListOf("are", "you", set, "?"),
            )

            val (response, stack) = defaultNodeManager.findTest(input)
            assertEquals(expectedResponse, response)
            assertEquals(expectedStack, stack)
        }
    }

    @Test
    fun `should be able to find a simple generic node`() {
        val input = "are you what ever shit ?"

        val expectedResponse = "Sorry. I cannot recognize what ever shit as a mood"
        val expectedStack = Stack(
            pattern = arrayListOf("are", "you", "what ever shit", "?"),
            star = arrayListOf("what ever shit")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should be able to find a simple specific node`() {
        val input = "are you alive ?"

        val expectedResponse = "No. I'm not alive"
        val expectedStack = Stack(
            pattern = arrayListOf("are", "you", "alive", "?")
        )

        val (response, stack) = defaultNodeManager.findTest(input)
        assertEquals(expectedResponse, response)
        assertEquals(expectedStack, stack)
    }

    @Test
    fun `should be able to expand a complex set pattern`() {
        val moods = arrayListOf(
            "happy",
            "sad",
            "boring"
        )

        val colors = arrayListOf(
            "red",
            "blue",
            "yellow"
        )

        for (mood in moods) {
            for (color in colors) {
                val input = "is a color $color a $mood color ?"

                val expectedResponse = "I think it is. A $color is really a $mood color"
                val expectedStack = Stack(
                    pattern = arrayListOf("is", "a", "color", color, "a", mood, "color", "?"),
                )

                val (response, stack) = defaultNodeManager.findTest(input)
                assertEquals(expectedResponse, response)
                assertEquals(expectedStack, stack)
            }
        }
    }

    @Test
    fun `should be able to expand a set pattern and mix it to wild v1`() {
        val moods = arrayListOf(
            "happy",
            "sad",
            "boring"
        )

        val colors = arrayListOf(
            "green",
            "black",
            "purple"
        )

        for (mood in moods) {
            for (color in colors) {
                val input = "is a color $color a $mood color ?"

                val expectedResponse = "I think it isn't. A $color isn't a $mood color"
                val expectedStack = Stack(
                    pattern = arrayListOf("is", "a", "color", color, "a", mood, "color", "?"),
                    star = arrayListOf(color)
                )

                val (response, stack) = defaultNodeManager.findTest(input)
                assertEquals(expectedResponse, response)
                assertEquals(expectedStack, stack)
            }
        }
    }

    @Test
    fun `should be able to expand a set pattern and mix it to wild v2`() {
        val moods = arrayListOf(
            "good",
            "peaceful",
            "romantic"
        )

        val colors = arrayListOf(
            "red",
            "blue",
            "yellow"
        )

        for (mood in moods) {
            for (color in colors) {
                val input = "is a color $color a $mood color ?"

                val expectedResponse = "Maybe. I'm not sure. A $color could be a $mood color"
                val expectedStack = Stack(
                    pattern = arrayListOf("is", "a", "color", color, "a", mood, "color", "?"),
                    star = arrayListOf(mood)
                )

                val (response, stack) = defaultNodeManager.findTest(input)
                assertEquals(expectedResponse, response)
                assertEquals(expectedStack, stack)
            }
        }
    }

    @Test
    fun `should be able to fallback to completely wild pattern`() {
        val moods = arrayListOf(
            "good",
            "peaceful",
            "romantic"
        )

        val colors = arrayListOf(
            "green",
            "black",
            "purple"
        )

        for (mood in moods) {
            for (color in colors) {
                val input = "is a color $color a $mood color ?"

                val expectedResponse = "Definitely not. A $color color will never be a $mood color"
                val expectedStack = Stack(
                    pattern = arrayListOf("is", "a", "color", color, "a", mood, "color", "?"),
                    star = arrayListOf(color, mood)
                )

                val (response, stack) = defaultNodeManager.findTest(input)
                assertEquals(expectedResponse, response)
                assertEquals(expectedStack, stack)
            }
        }
    }
}