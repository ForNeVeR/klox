package me.fornever.klox

abstract class Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitUnaryExpr(expr: Unary): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R
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
