package parser.xml

import org.simpleframework.xml.core.Persister
import parser.Parser
import java.io.InputStream
import kotlin.reflect.KClass

class XMLParserImpl<T : Any>(
    private val clazz: KClass<T>
) : Parser<T> {
    private val serializer = Persister()
    override fun parse(stream: InputStream): T =
        serializer.read(clazz.java, stream)
}