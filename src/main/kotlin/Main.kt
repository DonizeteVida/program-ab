import base.ConcreteResponse
import base.IncompleteResponse
import base.Node
import base.NodeManager
import extension.use
import parser.Parser
import parser.json.Aiml
import parser.json.JSONParseImpl
import java.io.File
import java.io.FileInputStream
import java.util.*

fun buildNodeTree(
    actual: Node,
    prev: Node?,
    args: List<String>,
    cursor: Int
): Pair<Node, Node?> {
    if (cursor + 1 !in args.indices) return actual to prev
    val pattern = args[cursor + 1]
    val next = actual[pattern] ?: Node(
        pattern,
        IncompleteResponse
    )
    actual[pattern] = next
    return buildNodeTree(next, actual, args, cursor + 1)
}

fun aimlToNodeManager(aimls: List<Aiml>): NodeManager {
    val nodeManager = NodeManager()

    aimls.map(Aiml::categories).flatten().map {
        val args = it.pattern.split(" ")
        val pattern = args[0]
        val parent = nodeManager[pattern] ?: Node(
            pattern,
            IncompleteResponse
        )
        nodeManager[pattern] = parent
        val (actual, prev) = buildNodeTree(parent, null, args, 0)
        if (prev == null) {
            nodeManager[pattern] = actual.copy(
                response = ConcreteResponse(it.template)
            )
        } else {
            prev[args.last()] = actual.copy(
                response = ConcreteResponse(it.template)
            )
        }
    }

    return nodeManager
}

fun main(args: Array<String>) {
    println("Hello World!")
    val parser: Parser<Aiml> = JSONParseImpl(Aiml::class)
    val files = File("./bots/jarvis/aiml").listFiles() ?: emptyArray()
    val aimls = files.map(::FileInputStream).use(parser::parse)

    val nodeManager = aimlToNodeManager(aimls)
    val scanner = Scanner(System.`in`)

    while (true) {
        print("Human: ")
        val line = scanner.nextLine()
        if (line.isEmpty()) break
        print("Bot: ")
        println(nodeManager.find(line))
    }
}