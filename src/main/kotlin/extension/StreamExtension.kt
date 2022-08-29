package extension

import java.io.InputStream

inline fun <T> List<InputStream>.use(block: (InputStream) -> T) =
    ArrayList<T>(size).apply {
        this@use.forEach {
            add(block(it))
            it.close()
        }
    }