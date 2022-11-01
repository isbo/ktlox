package io.github.isbo.ktlox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ParserTest {
    @Test
    fun scanBasicExpression() {
        val scanner = Scanner("6+3")
        val expr = Parser(scanner.scanTokens()).parse()
        assertEquals(BinaryExpr(LiteralExpr(5.0),
            Token(TokenType.PLUS, "+", null, 1), LiteralExpr(3.0)),
            expr)
    }

    @Test
    fun scanExpression() {
        val scanner = Scanner("5+3*(-1/10)")
        val expr = Parser(scanner.scanTokens()).parse()
        print(expr!!.astPrinter())
    }

}
