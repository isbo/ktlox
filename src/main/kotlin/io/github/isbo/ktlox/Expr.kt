package io.github.isbo.ktlox

sealed class Expr

data class CommaExpr(val operator: Token, val expressions: List<Expr>) : Expr()
data class TernaryExpr(val operator: Token, val condition: Expr, val trueExpr: Expr, val falseExpr: Expr) : Expr()
data class BinaryExpr(val left: Expr, val operator: Token, val right: Expr) : Expr()
data class UnaryExpr(val operator: Token, val right: Expr) : Expr()
data class LiteralExpr(val value: Any?) : Expr()
data class GroupingExpr(val expr: Expr) : Expr()

fun Expr.astPrinter(): String {
    return when(this) {
        is CommaExpr -> parenthesize(operator.lexeme, *expressions.toTypedArray())
        is TernaryExpr -> parenthesize(operator.lexeme, condition, trueExpr, falseExpr)
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