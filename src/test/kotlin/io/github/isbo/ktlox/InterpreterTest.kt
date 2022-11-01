package io.github.isbo.ktlox

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class InterpreterTest {

    @Test
    fun evaluateNumeric() {
        val scanner = Scanner("(6*5+3-8)/5")
        val expr = Parser(scanner.scanTokens()).parse()
        val result = expr?.evaluate()
        assertEquals(5.0, result as Double)
    }

    @Test
    fun evaluateBoolean() {
        val scanner = Scanner("(6+1) >= 5")
        val expr = Parser(scanner.scanTokens()).parse()
        val result = expr?.evaluate()
        assertTrue(result as Boolean)
    }

    @Test
    fun evaluateStringConcat() {
        val scanner = Scanner("\"one\" + \"two\"")
        val expr = Parser(scanner.scanTokens()).parse()
        val result = expr?.evaluate()
        assertEquals("onetwo", result as String)
    }
    @Test
    fun evaluateMixedStringConcat() {
        val scanner = Scanner("\"one\" + 2")
        val expr = Parser(scanner.scanTokens()).parse()
        val result = expr?.evaluate()
        assertEquals("one2", result as String)
    }

    @Test
    fun evaluateNullEquality() {
        val scanner = Scanner("nil == nil")
        val expr = Parser(scanner.scanTokens()).parse()
        val result = expr?.evaluate()
        assertTrue(result as Boolean)
    }

    @Test
    fun evaluateNullInEquality() {
        val scanner = Scanner("nil == \"str\"")
        val expr = Parser(scanner.scanTokens()).parse()
        val result = expr?.evaluate()
        assertFalse(result as Boolean)
    }
}