// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InterpreterTest {
    @Test
    fun `NaN equality`() =
        assertInterpretation("(0 / 0) == (0 / 0)", true)

    @Test
    fun `String plus operator`() =
        assertInterpretation("\"foo\" + 42", "foo42")

    private fun <T> assertInterpretation(input: String, result: T) {
        val expr = assertNotNull(Parser(Scanner(input).scanTokens()).parse())
        val interpreter = Interpreter()
        val evaluated = expr.accept(interpreter)
        assertEquals(result, evaluated)
    }
}
