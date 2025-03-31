// SPDX-FileCopyrightText: 2024-2025 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.Expr.*

class AstPrinter : Visitor<String> {
    fun print(expr: Expr) = expr.accept(this)

    override fun visitAssignExpr(expr: Assign) =
        parenthesize("assign ${expr.name.lexeme}", expr.value)

    override fun visitCallExpr(expr: Call) =
        print(expr.callee) + expr.arguments.joinToString { print(it) }
    override fun visitBinaryExpr(expr: Binary): String =
        parenthesize(expr.operator.lexeme, expr.left, expr.right)
    override fun visitGroupingExpr(expr: Grouping): String =
        parenthesize("group", expr.expression)
    override fun visitLiteralExpr(expr: Literal): String = when (expr.value) {
        null -> "nil"
        else -> expr.value.toString()
    }
    override fun visitLogicalExpr(expr: Logical) = parenthesize(expr.operator.lexeme, expr.left, expr.right)
    override fun visitUnaryExpr(expr: Unary): String =
        parenthesize(expr.operator.lexeme, expr.right)

    override fun visitTernaryExpr(expr: Ternary): String =
        parenthesize("?:", expr.condition, expr.ifTrue, expr.ifFalse)

    override fun visitVariable(expr: Variable): String = expr.name.lexeme
    override fun visitAnonymousFunction(expr: AnonymousFunction): String =
        "fun ${expr.params.joinToString()} { [BODY] }"

    private fun parenthesize(name: String, vararg exprs: Expr): String =
        "($name ${exprs.joinToString(" ") { it.accept(this) }})"
}
