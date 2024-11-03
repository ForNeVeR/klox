// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.Expr.*

class AstPrinter : Visitor<String> {
    fun print(expr: Expr) = expr.accept(this)

    override fun visitBinaryExpr(expr: Binary): String =
        parenthesize(expr.operator.lexeme, expr.left, expr.right)
    override fun visitGroupingExpr(expr: Grouping): String =
        parenthesize("group", expr.expression)
    override fun visitLiteralExpr(expr: Literal): String = when (expr.value) {
        null -> "nil"
        else -> expr.value.toString()
    }
    override fun visitUnaryExpr(expr: Unary): String =
        parenthesize(expr.operator.lexeme, expr.right)

    private fun parenthesize(name: String, vararg exprs: Expr): String =
        "($name ${exprs.joinToString(" ") { it.accept(this) }})"
}
