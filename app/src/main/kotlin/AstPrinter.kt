package me.fornever.klox

class AstPrinter : Expr.Visitor<String> {
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

class ReversePrinter : Expr.Visitor<String> {
    fun print(expr: Expr) = expr.accept(this)

    override fun visitBinaryExpr(expr: Binary): String =
        print(expr.left) + " " + print(expr.right) + " " + expr.operator.lexeme
    override fun visitGroupingExpr(expr: Grouping): String =
        print(expr)
    override fun visitLiteralExpr(expr: Literal): String = when (expr.value) {
        null -> "nil"
        else -> expr.value.toString()
    }
    override fun visitUnaryExpr(expr: Unary): String =
        print(expr.right) + " " + expr.operator.lexeme
}

fun main() {
    val expression = Binary(
        Binary(Literal(1.0), Token(TokenType.PLUS, "+", null, 1), Literal(2.0)),
        Token(TokenType.STAR, "*", null, 1),
        Binary(Literal(4.0), Token(TokenType.MINUS, "-", null, 1), Literal(3.0))
    )
    println(ReversePrinter().print(expression))
}
