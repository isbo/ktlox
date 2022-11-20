package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*
import java.lang.RuntimeException

class ParseError(val token: Token, override val message: String) : RuntimeException()

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            val statement = declaration()
            if (statement != null) statements.add(statement)
        }
        return statements
    }
    private fun declaration(): Stmt? {
        return try {
            if (match (VAR)) varDeclaration() else statement()
        } catch (e: ParseError) {
            synchronizeOnError()
            return null
        }
    }
    private fun synchronizeOnError() {
        // errata - this is simpler?
        while (!isAtEnd()) {
            when (peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
                SEMICOLON -> { advance(); return }
                else -> advance()
            }
        }
    }

    private fun varDeclaration(): Stmt {
        val name = consumeOrThrow(IDENTIFIER, "Expected variable name.")
        var initializer: Expr? = null
        if (match(EQUAL)) {
            initializer = expression()
        }
        consumeOrThrow(SEMICOLON, "Expected ';' after variable declaration.")
        return VarStmt(name, initializer)
    }

    private fun statement(): Stmt {
        return if (match(PRINT)) printStatement()
        else if (match(IF)) ifStatement()
        else if (match(LEFT_BRACE)) blockStatement()
        else if (match(WHILE)) whileStatement()
        else expressionStatement()
    }

    private fun whileStatement(): Stmt {
        consumeOrThrow(LEFT_PAREN, "Expected '(' after 'while'.")
        val condition = expression()
        consumeOrThrow(RIGHT_PAREN, "Expected ')' after 'while' condition.")
        val body = statement()
        return WhileStmt(condition, body)
    }

    private fun ifStatement(): Stmt {
        consumeOrThrow(LEFT_PAREN, "Expected '(' after 'if'.")
        val condition = expression()
        consumeOrThrow(RIGHT_PAREN, "Expected ')' after 'if' condition.")
        val thenBranch = statement()
        val elseBranch = if (match(ELSE)) statement() else null
        return IfStmt(condition, thenBranch, elseBranch)
    }

    private fun blockStatement(): Stmt {
        val statements = mutableListOf<Stmt>()
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            val statement = declaration()
            if (statement != null) statements.add(statement)
        }
        consumeOrThrow(RIGHT_BRACE, "Expected '}' at the end of the block.")
        return BlockStmt(statements)
    }

    private fun expressionStatement(): Stmt {
        val value = expression()
        consumeOrThrow(SEMICOLON, "Expect ; after expression.")
        return ExpressionStmt(value)
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consumeOrThrow(SEMICOLON, "Expect ; after value.")
        return PrintStmt(value)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = comma()

        if (match(EQUAL)) {
            if (expr is VariableExpr) {
                return AssignExpr(expr.name, comma())
            }
            error(previous(), "invalid assignment target.")
        }
        return expr
    }

    // comma has the lowest precedence - https://en.wikipedia.org/wiki/Comma_operator
    private fun comma(): Expr {
        var expr = ternary()

        while (match(COMMA)) {
            val operator = previous()
            expr = CommaExpr(expr, operator, ternary())
        }
        return expr
    }

    // FIXME - this is buggy precedence-wise
    private fun ternary(): Expr {
        var expr = or()

        if (match(QUESTION)) {
            val operator = previous()
            val trueExpr = or()
            consumeOrThrow(COLON, "Expected ':' in ternary expression.")
            val falseExpr = or()
            expr = TernaryExpr(operator, expr, trueExpr, falseExpr)
        }
        return expr
    }

    private fun or(): Expr {
        var expr = and()

        while (match(OR)) {
            val operator = previous()
            expr = LogicalExpr(expr, operator, and())
        }
        return expr
    }

    private fun and(): Expr {
        var expr = equality()
        while (match(AND)) {
            val operator = previous()
            expr = LogicalExpr(expr, operator, equality())
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return UnaryExpr(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TRUE)) return LiteralExpr(true)
        if (match(FALSE)) return LiteralExpr(false)
        if (match(NIL)) return LiteralExpr(null)

        if (match(NUMBER, STRING)) {
            return LiteralExpr(previous().literal)
        }

        if (match(IDENTIFIER)) {
            return VariableExpr(previous())
        }

        if (match(LEFT_PAREN)) {
            val expr = expression()
            consumeOrThrow(RIGHT_PAREN, "Expected ')' after expression.")
            return GroupingExpr(expr)
        }
        throw parseError(peek(), "Expected expression.")
    }

    private fun consumeOrThrow(tokenType: TokenType, message: String): Token {
        if (check(tokenType)) return advance()

        throw parseError(peek(), message)
    }

    private fun parseError(token: Token, message: String): ParseError {
        error(token, message)
        return ParseError(token, message)
    }

    private fun match(vararg tokenTypes: TokenType): Boolean {
        for (tokenType in tokenTypes) {
            if (check(tokenType)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(tokenType: TokenType): Boolean {
        return peek().type == tokenType
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return tokens[current].type == EOF
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

}

