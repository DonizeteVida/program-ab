package base.memory

import java.util.function.Supplier

data class Memory(
    val initial: HashMap<String, String> = hashMapOf(),
    val dynamic: HashMap<String, String> = hashMapOf()
) : Supplier<Memory> {
    operator fun get(index: String) = dynamic[index] ?: initial[index]

    operator fun set(index: String, value: String) {
        dynamic[index] = value
    }

    override fun get() = copy(
        dynamic = hashMapOf()
    )
}