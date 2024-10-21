// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

package me.fornever.klox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScannerTest {
    @Test
    fun `block comment`() {
        assertEquals(listOf(
            Token(TokenType.LEFT_PAREN, "(", null, 1),
            Token(TokenType.RIGHT_PAREN, ")", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ), scan("() /* () */"))
    }

    @Test
    fun `multiline block comment`() {
        assertEquals(listOf(
            Token(TokenType.LEFT_PAREN, "(", null, 1),
            Token(TokenType.RIGHT_PAREN, ")", null, 4),
            Token(TokenType.EOF, "", null, 4)
        ), scan("( /* (\n\n\n) */ )"))
    }
}

private fun scan(input: String): List<Token> {
    val scanner = Scanner(input)
    return scanner.scanTokens()
}
