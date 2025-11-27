package me.fornever.klox

import java.util.*

class Resolver(interpreter: Interpreter) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
    private val scopes = Stack<Map<String, Boolean>>()

    override fun visitBlockStmt(stmt: Stmt.Block): Unit {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    private fun beginScope() {
        scopes.push(mapOf())
    }
    private fun endScope() {
        scopes.pop()
    }

    private fun resolve(statements: List<Stmt>) = statements.forEach { resolve(it) }
    private fun resolve(stmt: Stmt) = stmt.accept(this)
    private fun resolve(expr: Expr) = expr.accept(this)
}
