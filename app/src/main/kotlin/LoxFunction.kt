// SPDX-FileCopyrightText: 2024-2025 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

class LoxFunction private constructor(
    private val name: String,
    private val params: List<Token>,
    private val body: List<Stmt>,
    private val closure: Environment
) : LoxCallable {

    constructor(declaration: Stmt.Function, closure: Environment) : this(
        declaration.name.lexeme,
        declaration.params,
        declaration.body,
        closure
    )

    constructor(declaration: Expr.AnonymousFunction, closure: Environment) : this(
        "anonymous",
        declaration.params,
        declaration.body,
        closure
    )

    override val arity = params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(closure)
        for (i in 0 until params.size) {
            environment.define(params[i].lexeme, arguments[i])
        }

        try {
            interpreter.executeBlock(body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }
        return null
    }

    override fun toString() = "<fn ${name}>"
}
