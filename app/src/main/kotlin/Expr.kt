// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

sealed class Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitUnaryExpr(expr: Unary): R
        fun visitTernaryExpr(expr: Ternary): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    data class Ternary(val condition: Expr, val ifTrue: Expr, val ifFalse: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) =
            visitor.visitTernaryExpr(this)
    }

    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitBinaryExpr(this)
    }

    data class Grouping(val expression: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitGroupingExpr(this)
    }
    data class Literal(val value: Any?) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitLiteralExpr(this)
    }
    data class Unary(val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitUnaryExpr(this)
    }
}
