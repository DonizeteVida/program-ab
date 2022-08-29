import extension.use
import parser.Parser
import parser.xml.Aiml
import parser.xml.XMLParserImpl
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
    println("Hello World!")
    val parser: Parser<Aiml> = XMLParserImpl(Aiml::class)
    val files = File("./bots/jarvis/aiml").listFiles() ?: emptyArray()
    val aimls = files.map(::FileInputStream).use(parser::parse)
    println(aimls)
}