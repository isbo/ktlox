package io.github.isbo.ktlox

sealed class Expr

class BinaryExpr(val left: Expr, val operator: Token, val right: Expr) : Expr()
class UnaryExpr(val operator: Token, val right: Expr) : Expr()
class LiteralExpr(val value: Any?) : Expr()
class GroupingExpr(val expr: Expr) : Expr()

fun Expr.astPrinter(): String {
    return when(this) {
        is BinaryExpr -> parenthesize(operator.lexeme, left, right)
        is UnaryExpr -> parenthesize(operator.lexeme, right)
        is LiteralExpr -> value.toString()
        is GroupingExpr -> parenthesize("group", expr)
    }
}

private fun parenthesize(name: String, vararg exprs: Expr): String {
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
    val expr: Expr = BinaryExpr(
        UnaryExpr(
            Token(TokenType.MINUS, "-", null, 1),
            LiteralExpr(123)
        ),
        Token(TokenType.STAR, "*", null, 1),
        GroupingExpr(
            LiteralExpr(45.67)
        )
    )

    print(expr.astPrinter())
}