package io.github.isbo.ktlox

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class InterpreterTest {
    private var interpreter: Interpreter? = null
    private var env: Environment? = null
    private val ac = mutableListOf<String>()
    private var printer: (message: Any?) -> Unit = { message -> ac.add(message.toString()) }

    @BeforeEach
    fun setup() {
        interpreter = Interpreter(printer = printer)
        env = Environment()
        ac.clear()
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
        val scanner = Scanner("var pi = 22/7; print(pi);")
        val stmts = Parser(scanner.scanTokens()).parse()
        interpreter!!.interpret(stmts)
        assertEquals(listOf("3.142857142857143"), ac)
    }
    @Test
    fun evaluateAssignment() {
        val scanner = Scanner("var pi = 22/7; pi=2.0;print(pi);")
        val stmts = Parser(scanner.scanTokens()).parse()
        interpreter!!.interpret(stmts)
        assertEquals(listOf("2"), ac)
    }
    @Test
    fun evaluateBlockVariableScope() {
        val scanner = Scanner("""var a = "global a";
            var b = "global b";
            var c = "global c";
            {
              var a = "outer a";
              var b = "outer b";
              {
                var a = "inner a";
                print a;
                print b;
                print c;
              }
              print a;
              print b;
              print c;
            }
            print a;
            print b;
            print c;""")
        val stmts = Parser(scanner.scanTokens()).parse()
        interpreter!!.interpret(stmts)
        assertEquals(listOf("inner a", "outer b", "global c", "outer a", "outer b", "global c",
        "global a", "global b", "global c"), ac)
    }

    @Test
    fun evaluateNestedIfElse() {
         val scanner = Scanner("""var a = 5;
            if (a < 10)
                if (a > 6) {
                    print("false");
                } else {
                    print("true");
                }
            """)
        val stmts = Parser(scanner.scanTokens()).parse()
        interpreter!!.interpret(stmts)
        assertEquals(listOf("true"), ac)
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