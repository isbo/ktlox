package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ScannerTest {

    @Test
    fun scanExpression() {
        val scanner = Scanner("3+4-1/(12.0*5)  // add")
        val tokens = scanner.scanTokens()
        assertEquals(listOf(NUMBER, PLUS, NUMBER, MINUS, NUMBER, SLASH,
            LEFT_PAREN, NUMBER, STAR, NUMBER, RIGHT_PAREN, EOF),
            tokens.map(Token::type))
    }

    @Test
    fun scanKeywords() {
        val scanner = Scanner("while(true){print \"hi\"}")
        val tokens = scanner.scanTokens()
        assertEquals(listOf(WHILE, LEFT_PAREN, TRUE, RIGHT_PAREN, LEFT_BRACE, PRINT,
            STRING, RIGHT_BRACE, EOF),
            tokens.map(Token::type))
    }

    @Test
    fun scanIdentifiers() {
        val scanner = Scanner("if(box) return \"hi\"")
        val tokens = scanner.scanTokens()
        assertEquals(listOf(IF, LEFT_PAREN, IDENTIFIER, RIGHT_PAREN, RETURN, STRING, EOF),
            tokens.map(Token::type))
    }
}