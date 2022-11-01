package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*
import java.lang.RuntimeException

class ParseError(val token: Token, override val message: String) : RuntimeException()

class Parser(val tokens: List<Token>) {
    var current = 0

    fun parse(): Expr? {
        return try {
            expression()
        } catch (error: ParseError) {
            null
        }
    }

    private fun expression(): Expr {
        return ternary()
    }

    private fun ternary(): Expr {
        var expr = comma()

        if (match(QUESTION)) {
            val operator = previous()
            val trueExpr = comma()
            consumeOrThrow(COLON, "Expected ':' in ternary expression.")
            val falseExpr = comma()
            expr = TernaryExpr(operator, expr, trueExpr, falseExpr)
        }
        return expr
    }

    private fun comma(): Expr {
        val expressions = mutableListOf<Expr>(equality())

        var operator: Token? = null
        while (match(COMMA)) {
            operator = previous()
            expressions.add(equality())
        }
        return if (operator == null) expressions[0] else CommaExpr(operator, expressions)
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

