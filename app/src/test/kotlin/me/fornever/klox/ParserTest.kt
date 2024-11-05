// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ParserTest {

    @Test
    fun `comma operator`() {
        doTest("2 + 2, 2 * 2",
            Expr.Binary(
                Expr.Binary(
                    Expr.Literal(2.0),
                    Token(TokenType.PLUS, "+", null, 1),
                    Expr.Literal(2.0)
                ),
                Token(TokenType.COMMA, ",", null, 1),
                Expr.Binary(
                    Expr.Literal(2.0),
                    Token(TokenType.STAR, "*", null, 1),
                    Expr.Literal(2.0)
                )
            )
        )
    }

    @Test
    fun `ternary operator`() {
        doTest("true ? 1 : 0",
            Expr.Ternary(
                Expr.Literal(true),
                Expr.Literal(1.0),
                Expr.Literal(0.0)
            )
        )
    }

    private fun doTest(input: String, expectedResult: Expr) {
        try {
            val tokens = Scanner(input).scanTokens()
            val parseResult = Parser(tokens).parse()
            assertFalse(Lox.hadError, "No parse error should be registered.")
            assertEquals(expectedResult, assertNotNull(parseResult))
        } finally {
            Lox.hadError = false
        }
    }
}
