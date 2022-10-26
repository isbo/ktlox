package io.github.isbo.ktlox

sealed class Expression

class Binary(val left: Expression, val operator: Token, val right: Expression) : Expression()
class Unary(val operator: Token,  val right: Expression) : Expression()
class Literal(val value: Any) : Expression()
class Grouping(val expr: Expression) : Expression()

fun Expression.astPrinter(): String {
    return when(this) {
        is Binary -> parenthesize(operator.lexeme, left, right)
        is Unary -> parenthesize(operator.lexeme, right)
        is Literal -> parenthesize(value.toString())
        is Grouping -> parenthesize("group", expr)
    }
}

private fun parenthesize(name: String, vararg exprs: Expression): String {
    val builder = StringBuilder()

    builder.append("(").append(name)
    for (expr in exprs) {
        builder.append(" ")
        builder.append(expr.astPrinter())
    }
    builder.append(")")

    return builder.toString()
}

fun main() {
    val expression: Expression = Binary(
        Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Literal(123)
        ),
        Token(TokenType.STAR, "*", null, 1),
        Grouping(
            Literal(45.67)
        )
    )

    print(expression.astPrinter())
}