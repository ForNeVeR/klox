// SPDX-FileCopyrightText: 2024-2026 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

class Environment(private val enclosing: Environment? = null) {
    private val values = mutableListOf<Any?>()

    fun define(value: Any?): Int {
        values.add(value)
        return values.size - 1
    }

    private fun ancestor(distance: Int): Environment {
        var environment: Environment? = this
        repeat(distance) {
            environment = environment?.enclosing
        }
        return environment!!
    }

    fun getAt(coords: VariableCoordinates): Any? =
        ancestor(coords.distance).values[coords.variableId]

    fun assignAt(coords: VariableCoordinates, value: Any?) {
        ancestor(coords.distance).values[coords.variableId] = value
    }

    fun get(name: Token, variableId: Int): Any? =
        values.getOrElse(variableId) {
            if (enclosing != null) return enclosing.get(name, variableId)
            throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        }
}

class VariableCoordinates(val distance: Int, val variableId: Int)
