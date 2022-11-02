package io.github.isbo.ktlox

sealed class Stmt

data class ExpressionStmt(val expression: Expr): Stmt()
data class PrintStmt(val expression: Expr): Stmt()

fun ExpressionStmt.execute() {
    expression.evaluate()
}

fun PrintStmt.execute() {
    val result = expression.evaluate()
    print(result)
}

fun Stmt.execute() {
    when(this) {
        is PrintStmt -> execute()
        is ExpressionStmt -> execute()
    }
}