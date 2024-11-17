package me.fornever.klox

class Environment {
    private val values = mutableMapOf<String, Any?>()
    fun define(name: String, value: Any?) {
        values[name] = value
    }
    fun get(name: Token): Any? =
        values.getOrElse(name.lexeme) {
            throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        }
}
