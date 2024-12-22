// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.TokenType.*

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Nothing?> {

    internal val globals = Environment().apply {
        define("clock", object : LoxCallable {
            override val arity = 0
            override fun call(interpreter: Interpreter, arguments: List<Any?>): Double {
                return System.currentTimeMillis() / 1000.0
            }
            override fun toString() = "<native fn>"
        })
    }
    private var environment = globals

    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements) {
                execute(statement)
            }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun evaluate(expr: Expr) = expr.accept(this)
    private fun execute(stmt: Stmt) = stmt.accept(this)

    internal fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            for (statement in statements) {
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitBlock(stmt: Stmt.Block): Nothing? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Nothing? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Nothing? {
        val function = LoxFunction(stmt, environment)
        environment.define(stmt.name.lexeme, function)
        return null
    }

    override fun visitIfStmt(stmt: Stmt.If): Nothing? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }

        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Nothing? {
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Nothing? {
        val value = stmt.value?.let(::evaluate)
        throw Return(value)
    }

    override fun visitVarStmt(stmt: Stmt.Var): Nothing? {
        val name = stmt.name.lexeme
        val initializer = stmt.initializer
        if (initializer != null) {
            val value = evaluate(initializer)
            environment.define(name, value)
        } else {
            environment.declare(name)
        }

        return null
    }

    private class BreakException : Exception()
    override fun visitWhileStmt(stmt: Stmt.While): Nothing? {
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body)
            } catch (_: BreakException) {
                break
            }
        }

        return null
    }

    override fun visitBreakStmt(stmt: Stmt.Break): Nothing? {
        throw BreakException()
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    override fun visitLiteralExpr(expr: Expr.Literal) = expr.value

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)
        if (expr.operator.type == OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }
        return evaluate(expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping) = evaluate(expr.expression)
    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            BANG -> !isTruthy(right)
            MINUS -> -(checkNumberOperand(expr.operator, right))
            else -> null // Unreachable.
        }
    }

    override fun visitVariable(expr: Expr.Variable) = environment.get(expr.name)

    private fun checkNumberOperand(operator: Token, operand: Any?): Double {
        if (operand is Double) return operand
        throw RuntimeError(operator, "Operand must be a number.")
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            MINUS -> checkNumberOperands(expr.operator, left, right) { l, r -> l - r}
            SLASH -> checkNumberOperands(expr.operator, left, right) { l, r ->
                if (r == 0.0) throw RuntimeError(expr.operator, "Division by zero.")
                l / r
            }
            STAR -> checkNumberOperands(expr.operator, left, right) { l, r -> l * r }
            PLUS -> when {
                left is Double && right is Double -> left + right
                left is String -> left + stringify(right)
                right is String -> stringify(left) + right
                else -> throw RuntimeError(expr.operator, "Operands must be two numbers or at least one of them should be a string.")
            }
            GREATER -> checkNumberOperands(expr.operator, left, right) { l, r -> l > r }
            GREATER_EQUAL -> checkNumberOperands(expr.operator, left, right) { l, r -> l >= r }
            LESS -> checkNumberOperands(expr.operator, left, right) { l, r -> l < r }
            LESS_EQUAL -> checkNumberOperands(expr.operator, left, right) { l, r -> l <= r}
            BANG_EQUAL -> !isEqual(left, right)
            EQUAL_EQUAL -> isEqual(left, right)
            else -> null // Unreachable.
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)
        val arguments = expr.arguments.map { evaluate(it) }
        return when (callee) {
            is LoxCallable -> {
                if (arguments.size != callee.arity) {
                    throw RuntimeError(expr.paren, "Expected ${callee.arity} arguments but got ${arguments.size}.")
                }

                callee.call(this, arguments)
            }
            else -> throw RuntimeError(expr.paren, "Can only call functions and classes.")
        }
    }

    private fun <T> checkNumberOperands(operator: Token, left: Any?, right: Any?, callback: (Double, Double) -> T): T {
        if (left is Double && right is Double) return callback(left, right)
        throw RuntimeError(operator, "Operands must be numbers.")
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

    fun stringify(obj: Any?) = when (obj) {
        null -> "nil"
        is Double -> {
            val text = obj.toString()
            if (text.endsWith(".0")) text.substring(0..text.length - 3)
            else text
        }
        else -> obj.toString()
    }
}
