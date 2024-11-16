// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

sealed class Stmt {
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitPrintStmt(stmt: Print): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    data class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitExpressionStmt(this)
    }
    data class Print(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitPrintStmt(this)
    }
}
