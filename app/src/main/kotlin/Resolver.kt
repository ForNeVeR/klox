// SPDX-FileCopyrightText: 2025-2026 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import java.util.*

class Resolver(private val interpreter: Interpreter) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
    private val scopes = Stack<Scope>()
    private var currentFunction = FunctionType.NONE

    override fun visitBlockStmt(stmt: Stmt.Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        resolve(stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt.params, stmt.body, FunctionType.FUNCTION)
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) resolve(stmt.elseBranch)
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        resolve(stmt.expression)
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.")
        }

        if (stmt.value != null) resolve(stmt.value)
    }

    override fun visitVarStmt(stmt: Stmt.Var) {
        declare(stmt.name)
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }
        define(stmt.name)
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        resolve(stmt.condition)
        resolve(stmt.body)
    }

    override fun visitBreakStmt(stmt: Stmt.Break) {
    }

    override fun visitAssignExpr(expr: Expr.Assign) {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
    }

    override fun visitBinaryExpr(expr: Expr.Binary) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitCallExpr(expr: Expr.Call) {
        resolve(expr.callee)

        for (argument in expr.arguments) {
            resolve(argument)
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping) {
        resolve(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal) {
    }

    override fun visitLogicalExpr(expr: Expr.Logical) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitUnaryExpr(expr: Expr.Unary) {
        resolve(expr.right)
    }

    override fun visitTernaryExpr(expr: Expr.Ternary) {
        resolve(expr.condition)
        resolve(expr.ifTrue)
        resolve(expr.ifFalse)
    }

    override fun visitAnonymousFunction(expr: Expr.AnonymousFunction) {
        resolveFunction(expr.params, expr.body, FunctionType.FUNCTION)
    }

    override fun visitVariableExpr(expr: Expr.Variable) {
        if (!scopes.isEmpty() && scopes.peek().variableByName(expr.name.lexeme)?.isDefined == false) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.")
        }

        resolveLocal(expr, expr.name)
    }

    private fun beginScope() {
        scopes.push(Scope())
    }
    private fun endScope() {
        val scope = scopes.pop()
        for ((name, variableInfo) in scope.allVariables()) {
            if (!variableInfo.isUsed) {
                Lox.error(variableInfo.declaration, "Variable '${name}' is never used.")
            }
        }
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) return

        val scope = scopes.peek()
        if (scope.variableByName(name.lexeme) != null) {
            Lox.error(name, "Already a variable with this name in this scope.")
        }

        scope.declareVariable(name)
    }

    private fun define(name: Token) {
        if (scopes.isEmpty()) return
        val scope = scopes.peek()
        val variableInfo = scope.variableByName(name.lexeme)!!
        scope.defineVariable(variableInfo)
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.size - 1 downTo 0) {
            val scope = scopes[i]
            val variableInfo = scope.variableByName(name.lexeme)
            if (variableInfo != null) {
                scope.markAsUsed(variableInfo)
                interpreter.resolve(
                    expr,
                    VariableCoordinates(
                        distance = scopes.size - 1 - i,
                        variableId = variableInfo.variableId
                    )
                )
                return
            }
        }
    }

    fun resolve(statements: List<Stmt>) {
        for (it in statements) {
            resolve(it)
        }
    }
    private fun resolve(stmt: Stmt) = stmt.accept(this)
    private fun resolve(expr: Expr) = expr.accept(this)

    private fun resolveFunction(
        params: List<Token>,
        body: List<Stmt>,
        @Suppress("SameParameterValue") // might improve in the future
        type: FunctionType) {
        val enclosingFunction = currentFunction
        currentFunction = type

        beginScope()
        for (parameter in params) {
            declare(parameter)
            define(parameter)
        }
        resolve(body)
        endScope()

        currentFunction = enclosingFunction
    }

    private enum class FunctionType {
        NONE,
        FUNCTION
    }
}

private class Scope {
    val variablesByName = mutableMapOf<String, VariableInfo>()

    fun variableByName(name: String): VariableInfo? = variablesByName[name]
    fun allVariables(): Map<String, VariableInfo> = variablesByName

    fun declareVariable(name: Token) {
        val nameString = name.lexeme
        if (variablesByName.containsKey(nameString)) error("Variable already declared: \"$nameString\".")
        variablesByName[nameString] = VariableInfo(
            name,
            variablesByName.size,
            isUsed = false,
            isDefined = false
        )
    }

    fun defineVariable(variableInfo: VariableInfo) {
        variablesByName[variableInfo.declaration.lexeme] = variableInfo.copy(isDefined = true)
    }
    fun markAsUsed(variableInfo: VariableInfo) {
        variablesByName[variableInfo.declaration.lexeme] = variableInfo.copy(isUsed = true)
    }
}

private data class VariableInfo(
    val declaration: Token,
    val variableId: Int,
    val isUsed: Boolean,
    val isDefined: Boolean
)
