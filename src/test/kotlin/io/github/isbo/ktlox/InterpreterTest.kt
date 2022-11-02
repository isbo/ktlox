package io.github.isbo.ktlox

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class InterpreterTest {

    @Test
    fun evaluateNumeric() {
        val scanner = Scanner("(6*5+3-8)/5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertEquals(5.0, result as Double)
    }

    @Test
    fun evaluateBoolean() {
        val scanner = Scanner("(6+1) >= 5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertTrue(result as Boolean)
    }

    @Test
    fun evaluateStringConcat() {
        val scanner = Scanner("\"one\" + \"two\";")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertEquals("onetwo", result as String)
    }

    @Test
    fun evaluateMixedStringConcat() {
        val scanner = Scanner("\"one\" + 2;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertEquals("one2", result as String)
    }

    @Test
    fun evaluateNullEquality() {
        val scanner = Scanner("nil == nil;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertTrue(result as Boolean)
    }

    @Test
    fun evaluateNullInEquality() {
        val scanner = Scanner("nil == \"str\";")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertFalse(result as Boolean)
    }
    @Test
    fun evaluateDivByZero() {
        val scanner = Scanner("5/(3-3.0);")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val e = assertThrows(RuntimeError::class.java) {
            expr.evaluate()
        }
        assertEquals(TokenType.SLASH, e.token.type)
    }
    @Test
    fun evaluateCommaExpression() {
        val scanner = Scanner("5+3*(-1/10), 52, 3.0>1.0, \"str\" == nil;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertFalse(result as Boolean)
    }
    @Test
    fun evaluateTernaryExpressionTruePart() {
        val scanner = Scanner("3.0>1.0 ? \"str\" == nil : 10/2*5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertFalse(result as Boolean)
    }
    @Test
    fun evaluateTernaryExpressionFalsePart() {
        val scanner = Scanner("3.0<1.0 ? \"str\" == nil : 15, 10/(2*5);")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate()
        assertEquals(1.0, result as Double)
    }
    @Test
    fun executeMultiStatements() {
        val scanner = Scanner("10+12-2; print \"ha\";")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(2, stmts.size)
        assertTrue(stmts[0] is ExpressionStmt)
        assertTrue(stmts[1] is PrintStmt)
    }
    @Test
    fun executeExprStatement() {
        val scanner = Scanner("10+12*5;")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(1, stmts.size)
        assertTrue(stmts[0] is ExpressionStmt)
    }
    @Test
    fun executePrintStatement() {
        val scanner = Scanner("print 2*5;")
        val stmts = Parser(scanner.scanTokens()).parse()
        assertEquals(1, stmts.size)
        assertTrue(stmts[0] is PrintStmt)
    }

}