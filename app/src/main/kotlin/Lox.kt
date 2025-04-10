// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.system.exitProcess

object Lox {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size > 1) {
            println("Usage: klox [script]")
            exitProcess(64)
        }
        else if (args.size == 1) {
            runFile(args[0])
        }
        else {
            runPrompt()
        }
    }

    private val interpreter = Interpreter()
    internal var hadError = false
    internal var hadRuntimeError = false
    private fun runFile(path: String) {
        val bytes = Files.readAllBytes(Path(path))
        run(String(bytes))

        if (hadError) exitProcess(65)
        if (hadRuntimeError) exitProcess(70)
    }

    private fun runPrompt() {
        val input = java.io.BufferedReader(java.io.InputStreamReader(System.`in`))
        while (true) {
            print("> ")
            val line = input.readLine() ?: break
            runOrEval(line)
            hadError = false
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()
        run(tokens)
    }

    private fun runOrEval(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()
        val expr = tryParseExpression(tokens)
        if (expr != null) {
            try {
                val result = expr.accept(interpreter)
                println(interpreter.stringify(result))
            } catch (err: RuntimeError) {
                runtimeError(err)
            }

            return
        }

        run(tokens)
    }

    private var silent = false
    private fun tryParseExpression(tokens: List<Token>): Expr? {
        val parser = Parser(tokens)
        var result: Expr? = null
        try {
            silent = true
            result = parser.parseExpression()
        } catch (e: Parser.ParseError) {
            // ignore
        } finally {
            silent = false
        }

        return result
    }

    private fun run(tokens: List<Token>) {
        val parser = Parser(tokens)
        val statements = parser.parse()

        // Stop if there was a syntax error.
        if (hadError) return

        interpreter.interpret(statements)
    }

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    private fun report(line: Int, where: String, message: String) {
        if (silent) return
        System.err.println("[line $line] Error $where: $message")
        hadError = true
    }

    fun error(token: Token, message: String) {
        if (token.type == TokenType.EOF) {
            report(token.line, "at end", message)
        }
        else {
            report(token.line, "at '${token.lexeme}'", message)
        }
    }

    fun runtimeError(error: RuntimeError) {
        System.err.println(error.message + "\n[line: ${error.token.line}]")
        hadRuntimeError = true
    }
}


