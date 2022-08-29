package parser.xml

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import parser.Parser
import java.io.InputStream
import kotlin.reflect.KClass

class XMLParserImpl<T : Any>(
    private val clazz: KClass<T>
) : Parser<T> {
    private val xmlMapper = XmlMapper.builder().apply {
        defaultUseWrapper(false)
        configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.INDENT_OUTPUT, true)
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true)
    }.build()

    override fun parse(stream: InputStream): T =
        xmlMapper.readValue(stream, clazz.java)
}