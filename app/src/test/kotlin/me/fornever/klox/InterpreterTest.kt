// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.testFramework.doTestWithStdErr
import kotlin.test.*

class InterpreterTest {
    @Test
    fun `String plus operator`() =
        assertInterpretation("\"foo\" + 42", "foo42")

    @Test
    fun `Division by zero`() =
        assertInterpretationError("42 / 0", "Division by zero.\n[line: 1]\n")

    @Suppress("SameParameterValue")
    private fun <T> assertInterpretation(input: String, expectedResult: T) {
        val result = doTestWithStdErr {
            val expr = assertNotNull(Parser(Scanner(input).scanTokens()).parse())
            val interpreter = Interpreter()
            val evaluated = expr.accept(interpreter)
            assertEquals(expectedResult, evaluated)
        }

        assertFalse(result.hadError)
        assertEquals("", result.stdErr)
    }

    @Suppress("SameParameterValue")
    private fun assertInterpretationError(input: String, error: String) {
        val result = doTestWithStdErr {
            val expr = assertNotNull(Parser(Scanner(input).scanTokens()).parse())
            val interpreter = Interpreter()
            interpreter.interpret(expr)
        }

        assertTrue(result.hadRuntimeError)
        assertEquals(error, result.stdErr)
    }
}
