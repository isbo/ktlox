package io.github.isbo.ktlox

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ParserTest {
    @Test
    fun scanBinaryExpression() {
        val scanner = Scanner("5+3;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        assertEquals(BinaryExpr(LiteralExpr(5.0),
            Token(TokenType.PLUS, "+", null, 1), LiteralExpr(3.0)),
            expr)
    }

    @Test
    fun scanCommaExpression() {
        val scanner = Scanner("5+3*(-1/10), 52, 3.0>1.0, \"str\" == nil;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        assertTrue(expr is CommaExpr)
        assertEquals(4, (expr as CommaExpr).expressions.size)
    }

    @Test
    fun scanTernaryExpression() {
        val scanner = Scanner("3.0>1.0 ? \"str\" == nil : 10/2*5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        assertTrue(expr is TernaryExpr)
    }
    @Test
    fun parseStatementWithoutCommaThrows() {
        val scanner = Scanner("10+12-2")
        assertThrows(ParseError::class.java) {
            Parser(scanner.scanTokens()).parse()
        }
    }

}
