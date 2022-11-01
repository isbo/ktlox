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
        is TernaryExpr -> evaluate()
        is CommaExpr -> evaluate()
        is BinaryExpr -> evaluate()
        is UnaryExpr -> evaluate()
        is LiteralExpr -> value
        is GroupingExpr -> evaluate()
    }
}

fun TernaryExpr.evaluate(): Any? {
    return if (condition.evaluate().isTruthy()) trueExpr.evaluate() else falseExpr.evaluate()
}

fun CommaExpr.evaluate(): Any? {
    // evaluate all, return last expression's value
    var result: Any? = null
    for (expr in expressions) {
        result = expr.evaluate()
    }
    return result
}

fun BinaryExpr.evaluate(): Any? {
    val left = left.evaluate()
    val right = right.evaluate()

    try {
        return when (operator.type) {
            PLUS -> {
                if (left is Double && right is Double)
                    left + right
                else if (left is String || right is String)
                    left.toString() + right.toString()
                else throw RuntimeError(operator, "Operands must be numbers or strings")
            }

            MINUS -> left as Double - right as Double
            SLASH -> {
                val denom = right as Double
                if (denom == 0.0)
                    throw RuntimeError(operator, "Denominator must be non-zero")
                left as Double / right
            }
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
        return !right.isTruthy()
    }
    return null // unreachable
}

fun Any?.isTruthy(): Boolean {
    if (this == null) return false
    if (this is Boolean) return this

    return true
}
