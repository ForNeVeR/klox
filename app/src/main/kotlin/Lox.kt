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

    private var hadError = false
    private fun runFile(path: String) {
        val bytes = Files.readAllBytes(Path(path))
        run(String(bytes))

        if (hadError) exitProcess(65)
    }

    private fun runPrompt() {
        val input = java.io.BufferedReader(java.io.InputStreamReader(System.`in`))
        while (true) {
            print("> ")
            val line = input.readLine() ?: break
            run(line)
            hadError = false
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()
        for (token in tokens) {
            println(token)
        }
    }

    private fun error(line: Int, message: String) {
        report(line, "", message)
    }

    private fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error $where: $message")
        hadError = true
    }
}


