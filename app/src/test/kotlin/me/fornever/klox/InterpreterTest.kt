// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InterpreterTest {
    @Test
    fun `NaN equality`() {
        val input = "(0 / 0) == (0 / 0)"
        val expr = assertNotNull(Parser(Scanner(input).scanTokens()).parse())
        val result = expr.accept(Interpreter())
        assertEquals(true, result)
    }
}
