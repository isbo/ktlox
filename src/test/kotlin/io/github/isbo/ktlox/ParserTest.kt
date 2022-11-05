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
    fun scanVarStatement() {
        val scanner = Scanner("var pi = 3.14;")
        val stmt = Parser(scanner.scanTokens()).parse()[0]
        assertTrue(stmt is VarStmt)
        val expected = VarStmt(
            Token(TokenType.IDENTIFIER, "pi", null, 1),
            LiteralExpr(3.14)
        )
        assertEquals(expected, stmt)
    }
    @Test
    fun parseStatementWithoutComma() {
        val scanner = Scanner("10+12-2")
        val statements = Parser(scanner.scanTokens()).parse()
        assertTrue(statements.isEmpty())
    }
   @Test
    fun parseMultiStatements() {
        val scanner = Scanner("10+12-2; print \"ha\";")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(2, stmts.size)
        assertTrue(stmts[0] is ExpressionStmt)
        assertTrue(stmts[1] is PrintStmt)
    }
    @Test
    fun parseVarStatements() {
        val scanner = Scanner("val i = 10+12-2; i;")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(2, stmts.size)
        assertTrue(stmts[0] is ExpressionStmt)
        assertTrue(stmts[1] is PrintStmt)
    }
    @Test
    fun parseExprStatement() {
        val scanner = Scanner("10+12*5;")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(1, stmts.size)
        assertTrue(stmts[0] is ExpressionStmt)
    }
    @Test
    fun parsePrintStatement() {
        val scanner = Scanner("print 2*5;")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(1, stmts.size)
        assertTrue(stmts[0] is PrintStmt)
    }

}
