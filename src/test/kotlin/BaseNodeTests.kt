import base.DefaultNodeManagerBuilder
import extension.use
import parser.Parser
import parser.json.JSONParseImpl
import parser.json.Knowledge
import java.io.File
import java.io.FileInputStream

abstract class BaseNodeTests(
    path: String
) {
    private val parser: Parser<Knowledge> = JSONParseImpl(Knowledge::class)

    private val files = File("./bots/$path").listFiles() ?: emptyArray()
    private val data = files.map(::FileInputStream).use(parser::parse)

    val defaultNodeManager = DefaultNodeManagerBuilder.build(data)
}