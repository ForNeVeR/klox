// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.testFramework.doTestWithStdIo
import kotlin.test.*

class InterpreterTest {
    @Test
    fun `string plus operator`() =
        assertInterpretation("\"foo\" + 42", "foo42")

    @Test
    fun `division by zero`() =
        assertInterpretationError("42 / 0", "Division by zero.\n[line: 1]\n")

    @Test
    fun `break instruction`() = assertOutput("""
        var a = 0;
        while (true) {
            a = a + 1;
            print a;
            if (a == 5) break;
        }
    """.trimIndent(), "1\n2\n3\n4\n5\n")

    @Suppress("SameParameterValue")
    private fun <T> assertInterpretation(input: String, expectedResult: T) {
        val result = doTestWithStdIo {
            val stmt = Parser(Scanner("$input;").scanTokens()).parse().single()
            val expr = assertNotNull((stmt as Stmt.Expression).expression)
            val interpreter = Interpreter()
            val evaluated = expr.accept(interpreter)
            assertEquals(expectedResult, evaluated)
        }

        assertFalse(result.hadError)
        assertEquals("", result.stdErr)
    }

    @Suppress("SameParameterValue")
    private fun assertInterpretationError(input: String, error: String) {
        val result = doTestWithStdIo {
            val expr = assertNotNull(Parser(Scanner("$input;").scanTokens()).parse())
            val interpreter = Interpreter()
            interpreter.interpret(expr)
        }

        assertTrue(result.hadRuntimeError)
        assertEquals(error, result.stdErr)
    }

    private fun assertOutput(input: String, @Suppress("SameParameterValue") expectedOutput: String) {
        val result = doTestWithStdIo {
            val statements = assertNotNull(Parser(Scanner(input).scanTokens()).parse())
            val interpreter = Interpreter()
            interpreter.interpret(statements)
        }

        assertEquals("", result.stdErr)
        assertFalse(result.hadError)
        assertFalse(result.hadRuntimeError)
        assertEquals(expectedOutput, result.stdOut)
    }
}
