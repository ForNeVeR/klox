// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

class Environment(private val enclosing: Environment? = null) {
    private val declaredVariables = mutableSetOf<String>()
    private val values = mutableMapOf<String, Any?>()

    fun declare(name: String) {
        declaredVariables.add(name)
    }

    fun define(name: String, value: Any?) {
        declaredVariables.add(name)
        values[name] = value
    }

    fun get(name: Token): Any? =
        values.getOrElse(name.lexeme) {
            if (enclosing != null) return enclosing.get(name)
            throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        }

    fun assign(name: Token, value: Any?) {
        val varName = name.lexeme
        if (declaredVariables.contains(varName) || values.containsKey(varName)) {
            values[varName] = value
            return
        }

        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }

        throw RuntimeError(name, "Undefined variable '${varName}'.")
    }
}
