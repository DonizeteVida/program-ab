import extension.use
import parser.Parser
import parser.json.Aiml
import parser.json.JSONParseImpl
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
    println("Hello World!")
    val parser: Parser<Aiml> = JSONParseImpl(Aiml::class)
    val files = File("./bots/jarvis/aiml").listFiles() ?: emptyArray()
    val aimls = files.map(::FileInputStream).use(parser::parse)
    println(aimls)
}