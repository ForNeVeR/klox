package me.fornever.klox

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
) {
    override fun toString() = "$type $lexeme $literal"
}
