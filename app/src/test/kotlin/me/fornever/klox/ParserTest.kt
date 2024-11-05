// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import kotlin.test.*

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

    @Test
    fun `error production`() {
        doTestWithError(
            "+2",
            Expr.Literal(2.0),
            "[line 1] Error at '+': Binary operator should include both terms.${System.lineSeparator()}")
    }

    private fun doTestWithError(input: String, expectedResult: Expr, expectedMessage: String) {
        val output = ByteArrayOutputStream()
        PrintStream(output, true, StandardCharsets.UTF_8).use { ps ->
            val previousOutput = System.err
            System.setErr(ps)
            try {
                val tokens = Scanner(input).scanTokens()
                val parseResult = Parser(tokens).parse()
                assertEquals(expectedResult, parseResult)
                assertTrue(Lox.hadError, "Expected a parse error.")

                val error = output.toString(StandardCharsets.UTF_8)
                assertEquals(expectedMessage, error)
            } finally {
                Lox.hadError = false
                System.setErr(previousOutput)
            }
        }
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
