package parser.json

import com.google.gson.Gson
import parser.Parser
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

internal val gson = Gson()

class JSONParseImpl<T : Any>(
    private val clazz: KClass<T>
) : Parser<T> {
    override fun parse(stream: InputStream): T =
        gson.fromJson(InputStreamReader(stream), clazz.java)
}