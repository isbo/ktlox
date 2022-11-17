package io.github.isbo.ktlox

sealed class Expr

data class AssignExpr(val name: Token, val value: Expr) : Expr()
data class CommaExpr(val left: Expr, val operator: Token, val right: Expr) : Expr()
data class TernaryExpr(val operator: Token, val condition: Expr, val trueExpr: Expr, val falseExpr: Expr) : Expr()
data class BinaryExpr(val left: Expr, val operator: Token, val right: Expr) : Expr()
data class LogicalExpr(val left: Expr, val operator: Token, val right: Expr) : Expr()
data class UnaryExpr(val operator: Token, val right: Expr) : Expr()
data class LiteralExpr(val value: Any?) : Expr()
data class GroupingExpr(val expr: Expr) : Expr()
data class VariableExpr(val name: Token) : Expr()

fun Expr.astPrinter(): String {
    return when(this) {
        is AssignExpr -> parenthesize(name.lexeme, value)
        is CommaExpr -> parenthesize(operator.lexeme, left, right)
        is TernaryExpr -> parenthesize(operator.lexeme, condition, trueExpr, falseExpr)
        is BinaryExpr -> parenthesize(operator.lexeme, left, right)
        is LogicalExpr -> parenthesize(operator.lexeme, left, right)
        is UnaryExpr -> parenthesize(operator.lexeme, right)
        is LiteralExpr -> value.toString()
        is GroupingExpr -> parenthesize("group", expr)
        is VariableExpr -> parenthesize(name.lexeme)
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
