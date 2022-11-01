package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*
import java.lang.ClassCastException
import java.lang.RuntimeException


class Interpreter {

    fun interpret(expr: Expr) {
        try {
            val result = expr.evaluate()
            print(result)
        } catch (e: RuntimeError) {
            runtimeError(e)
        }
    }
}

class RuntimeError(val token: Token, override val message: String) : RuntimeException()

// TODO: revisit and remove?
fun Expr.evaluate(): Any? {
    return when (this) {
        is BinaryExpr -> evaluate()
        is UnaryExpr -> evaluate()
        is LiteralExpr -> value
        is GroupingExpr -> evaluate()
    }
}

fun BinaryExpr.evaluate(): Any? {
    val left = left.evaluate()
    val right = right.evaluate()

    try {
        return when (operator.type) {
            PLUS -> {
                if (left is Double && right is Double)
                    left + right
                else if (left is String && right is String)
                    left + right
                else throw RuntimeError(operator, "Operands must be numbers or strings")
            }

            MINUS -> left as Double - right as Double
            SLASH -> left as Double / right as Double
            STAR -> left as Double * right as Double
            GREATER -> left as Double > right as Double
            GREATER_EQUAL -> left as Double >= right as Double
            LESS -> (left as Double) < right as Double
            LESS_EQUAL -> left as Double <= right as Double
            EQUAL_EQUAL -> left == right
            BANG_EQUAL -> left != right
            else -> null // unreachable
        }
    } catch (e: ClassCastException) {
        throw RuntimeError(operator, "Operand(s) must be number(s)")
    }
}


fun GroupingExpr.evaluate(): Any? {
    return expr.evaluate()
}

fun UnaryExpr.evaluate(): Any? {
    val right = right.evaluate()

    try {
        if (operator.type == MINUS) {
            return -(right as Double)
        }
    } catch (e: ClassCastException) {
        throw RuntimeError(operator, "Operand must be number")
    }
    if (operator.type == BANG) {
        return !isTruthy(right)
    }
    return null // unreachable
}

fun isTruthy(right: Any?): Boolean {
    if (right == null) return false
    if (right is Boolean) return right

    return true
}
