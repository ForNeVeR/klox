// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

sealed class Stmt {
    interface Visitor<R> {
        fun visitBlock(stmt: Block): R
        fun visitExpressionStmt(stmt: Expression): R
        fun visitIfStmt(stmt: If): R
        fun visitPrintStmt(stmt: Print): R
        fun visitVarStmt(stmt: Var): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    data class Block(val statements: List<Stmt>) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitBlock(this)
    }
    data class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitExpressionStmt(this)
    }
    data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitIfStmt(this)
    }
    data class Print(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitPrintStmt(this)
    }
    data class Var(val name: Token, val initializer: Expr?) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitVarStmt(this)
    }
}
