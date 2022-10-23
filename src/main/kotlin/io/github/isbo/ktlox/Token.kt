package io.github.isbo.ktlox

class Token(private val type: TokenType,
            private val lexeme: String,
            private val literal: Any?,
            private val line: Int) {

    override fun toString(): String {
        return "$type $lexeme $literal"
    }
}