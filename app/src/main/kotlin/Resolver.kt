package me.fornever.klox

class Resolver(interpreter: Interpreter) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
}
