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

fun main() {
    val expression = Binary(
        Unary(Token(TokenType.MINUS, "-", null, 1), Literal(123)),
        Token(TokenType.STAR, "*", null, 1),
        Grouping(Literal(45.67))
    )
    println(AstPrinter().print(expression))
}
