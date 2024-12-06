// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import me.fornever.klox.testFramework.doTestWithStdIo
import kotlin.test.*

class ExprParserTest {

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

    @Test
    fun `error production`() {
        doTestWithError(
            "+2",
            Expr.Literal(2.0),
            "[line 1] Error at '+': Binary operator should include both terms.\n")
    }

    @Suppress("SameParameterValue")
    private fun doTestWithError(input: String, expectedResult: Expr, expectedMessage: String) {
        val result = doTestWithStdIo {
            val tokens = Scanner("$input;").scanTokens()
            val parseResult = (Parser(tokens).parse().single() as Stmt.Expression).expression
            assertEquals(expectedResult, parseResult)
        }

        assertTrue(result.hadError, "Expected a parse error.")
        assertEquals(expectedMessage, result.stdErr)
    }

    private fun doTest(input: String, expectedResult: Expr) {
        val result = doTestWithStdIo {
            val tokens = Scanner("$input;").scanTokens()
            val parseResult = (Parser(tokens).parse().single() as Stmt.Expression).expression
            assertEquals(expectedResult, assertNotNull(parseResult))
        }

        assertFalse(result.hadError, "Expected no errors.")
        assertEquals("", result.stdErr)
    }
}
