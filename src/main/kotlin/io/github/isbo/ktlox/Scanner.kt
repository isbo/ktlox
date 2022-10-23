package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*

class Scanner(private val source: String) {
    private val tokens: MutableList<Token> = ArrayList()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while(!isAtEnd()) {
            scanToken()
            start = current
        }
        addToken(EOF, null)
        return tokens
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start until current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun scanToken() {
        val c = advance()
        when(c) {
            // single char
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)

            // single or double char
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)

            // to be ignored - whitespace, comments
            '/' -> if (match('/')) {
                while (!isAtEnd() && peek() != '\n') advance()
            } else {
                addToken(SLASH)
            }
            ' ', '\r', '\t' -> Unit
            '\n' -> line++

            // literals
            '"' -> tokenizeString()

            // keywords

            else -> {
                if (c.isDigit()) tokenizeNumber()
                else if (c.isLetter()) tokenizeIdentifer()
                else error(line, "Unexpected character $c")
            }
        }
    }

    private fun tokenizeIdentifer() {
        TODO("Not yet implemented")
    }

    private fun tokenizeNumber() {
        TODO("Not yet implemented")
    }

    private fun tokenizeString() {
        while (peek() != '"' && !isAtEnd()) {
            val c = advance()
            if (c == '\n') line++
        }
        val value = source.substring(start+1 until  current)
        if (isAtEnd()) {
            error(line, "Unterminated string: $value")
            return
        }
        advance() // closing "

        addToken(STRING, value)
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[current] != expected) return false
        current++
        return true
    }

    private fun addToken(type: TokenType) = addToken(type, null)

    private fun advance() = source[current++]

    private fun isAtEnd() = current >= source.length


}