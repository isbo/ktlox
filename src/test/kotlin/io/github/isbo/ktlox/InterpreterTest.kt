package io.github.isbo.ktlox

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class InterpreterTest {
    private var interpreter: Interpreter? = null
    private var env: Environment? = null
    @BeforeEach
    fun setup() {
        interpreter = Interpreter()
        env = Environment()
    }
    @Test
    fun evaluateNumeric() {
        val scanner = Scanner("(6*5+3-8)/5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertEquals(5.0, result as Double)
    }

    @Test
    fun evaluateBoolean() {
        val scanner = Scanner("(6+1) >= 5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertTrue(result as Boolean)
    }

    @Test
    fun evaluateStringConcat() {
        val scanner = Scanner("\"one\" + \"two\";")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertEquals("onetwo", result as String)
    }

    @Test
    fun evaluateMixedStringConcat() {
        val scanner = Scanner("\"one\" + 2;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertEquals("one2", result as String)
    }

    @Test
    fun evaluateNullEquality() {
        val scanner = Scanner("nil == nil;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertTrue(result as Boolean)
    }

    @Test
    fun evaluateNullInEquality() {
        val scanner = Scanner("nil == \"str\";")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertFalse(result as Boolean)
    }
    @Test
    fun evaluateDivByZero() {
        val scanner = Scanner("5/(3-3.0);")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val e = assertThrows(RuntimeError::class.java) {
            expr.evaluate(env!!)
        }
        assertEquals(TokenType.SLASH, e.token.type)
    }
    @Test
    fun evaluateCommaExpression() {
        val scanner = Scanner("5+3*(-1/10), 52, 3.0>1.0, \"str\" == nil;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertFalse(result as Boolean)
    }
    @Test
    fun evaluateTernaryExpressionTruePart() {
        val scanner = Scanner("3.0>1.0 ? \"str\" == nil : 10/2*5;")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertFalse(result as Boolean)
    }
    @Test
    fun evaluateTernaryExpressionFalsePart() {
        val scanner = Scanner("3.0<1.0 ? \"str\" == nil : 15, 10/(2*5);")
        val expr = (Parser(scanner.scanTokens()).parse()[0] as ExpressionStmt).expression
        val result = expr.evaluate(env!!)
        assertEquals(1.0, result as Double)
    }
    @Test
    fun evaluateVarExpression() {
        val scanner = Scanner("var pi = 22/7; pi;")
        val stmts = Parser(scanner.scanTokens()).parse()
        interpreter!!.interpret(stmts)
        assertTrue(stmts[0] is VarStmt)
        assertEquals(22/7.0, interpreter!!.env.get(Token(TokenType.IDENTIFIER, "pi", null, 0)))
    }
    @Test
    fun evaluateAssignment() {
        val scanner = Scanner("var pi = 22/7; pi=2.0;")
        val stmts = Parser(scanner.scanTokens()).parse()
        interpreter!!.interpret(stmts)
        assertTrue(stmts[0] is VarStmt)
        assertEquals(2.0, interpreter!!.env.get(Token(TokenType.IDENTIFIER, "pi", null, 0)))
    }

    /* TODO: uncomment when we can catch errors via API
   @Test
    fun evaluateAssigningUndefinedThrows() {
        val scanner = Scanner("pi=2.0;")
        val stmts = Parser(scanner.scanTokens()).parse()
        val e = assertThrows(RuntimeError::class.java) {
            interpreter!!.interpret(stmts)
        }
    }
    */
}