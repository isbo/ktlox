package io.github.isbo.ktlox

sealed class Stmt

data class ExpressionStmt(val expression: Expr): Stmt()
data class PrintStmt(val expression: Expr): Stmt()
data class VarStmt(val name: Token, val initializer: Expr?): Stmt()
data class BlockStmt(val statements: List<Stmt>): Stmt()
data class IfStmt(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?): Stmt()
data class WhileStmt(val condition: Expr, val body: Stmt): Stmt()
