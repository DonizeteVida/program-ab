package parser

import java.io.InputStream

interface Parser<T : Any> {
    fun parse(stream: InputStream): T
}