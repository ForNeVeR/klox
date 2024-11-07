// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.TokenType.*

class Interpreter : Expr.Visitor<Any?> {
    private fun evaluate(expr: Expr) = expr.accept(this)

    override fun visitLiteralExpr(expr: Expr.Literal) = expr.value
    override fun visitGroupingExpr(expr: Expr.Grouping) = evaluate(expr.expression)
    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            BANG -> !isTruthy(right)
            MINUS -> -(right as Double)
            else -> null // Unreachable.
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            MINUS -> left as Double - right as Double
            SLASH -> left as Double / right as Double
            STAR -> left as Double * right as Double
            PLUS -> when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                else -> null
            }
            GREATER -> left as Double > right as Double
            GREATER_EQUAL -> left as Double >= right as Double
            LESS -> (left as Double) < right as Double
            LESS_EQUAL -> (left as Double) <= right as Double
            BANG_EQUAL -> !isEqual(left, right)
            EQUAL_EQUAL -> isEqual(left, right)
            else -> null // Unreachable.
        }
    }

    override fun visitTernaryExpr(expr: Expr.Ternary): Any? {
        val condition = evaluate(expr.condition)
        return if (isTruthy(condition)) evaluate(expr.ifTrue) else evaluate(expr.ifFalse)
    }

    private fun isTruthy(value: Any?) = when(value) {
        null -> false
        is Boolean -> value
        else -> true
    }

    private fun isEqual(left: Any?, right: Any?): Boolean = left == right
}
