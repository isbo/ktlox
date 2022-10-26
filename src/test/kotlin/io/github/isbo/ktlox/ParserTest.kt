package io.github.isbo.ktlox

import org.junit.jupiter.api.Test

internal class ParserTest {

    @Test
    fun scanExpression() {
        val scanner = Scanner("5+3*(-1/10)")
        val expr = Parser(scanner.scanTokens()).parse()
        print(expr!!.astPrinter())
    }

}
