import base.processor.NodeManager
import extension.use
import parser.Parser
import parser.json.Aiml
import parser.json.JSONParseImpl
import java.io.File
import java.io.FileInputStream
import java.util.*

fun main(args: Array<String>) {
    println("Hello World!")
    val parser: Parser<Aiml> = JSONParseImpl(Aiml::class)
    val files = File("./bots/jarvis/").listFiles() ?: emptyArray()
    val data = files.map(::FileInputStream).use(parser::parse)

    val nodeManager = NodeManager.build(data)

    val scanner = Scanner(System.`in`)

    while (true) {
        print("Human: ")
        val line = scanner.nextLine()
        if (line.isEmpty()) break
        print("Bot: ")
        println(nodeManager.find(line))
    }
}