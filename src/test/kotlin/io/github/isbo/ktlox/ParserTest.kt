package io.github.isbo.ktlox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ParserTest {
    @Test
    fun scanBinaryExpression() {
        val scanner = Scanner("5+3")
        val expr = Parser(scanner.scanTokens()).parse()
        assertEquals(BinaryExpr(LiteralExpr(5.0),
            Token(TokenType.PLUS, "+", null, 1), LiteralExpr(3.0)),
            expr)
    }

    @Test
    fun scanCommaExpression() {
        val scanner = Scanner("5+3*(-1/10), 52, 3.0>1.0, \"str\" == nil")
        val expr = Parser(scanner.scanTokens()).parse()
        assertTrue(expr is CommaExpr)
        assertEquals(4, (expr as CommaExpr).expressions.size)
    }

    @Test
    fun scanTernaryExpression() {
        val scanner = Scanner("3.0>1.0 ? \"str\" == nil : 10/2*5")
        val expr = Parser(scanner.scanTokens()).parse()
        assertTrue(expr is TernaryExpr)
    }
}
