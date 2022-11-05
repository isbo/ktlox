package io.github.isbo.ktlox

sealed class Stmt

data class ExpressionStmt(val expression: Expr): Stmt()
data class PrintStmt(val expression: Expr): Stmt()
data class VarStmt(val name: Token, val initializer: Expr?): Stmt()
