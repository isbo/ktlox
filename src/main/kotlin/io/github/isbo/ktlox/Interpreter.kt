package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*
import java.lang.ClassCastException

class Interpreter {
    val env = Environment()

    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements) {
                statement.execute(env)
            }
        } catch (e: RuntimeError) {
            runtimeError(e)
        }
    }
}

fun ExpressionStmt.execute(env: Environment) {
    expression.evaluate(env)
}

fun PrintStmt.execute(env: Environment) {
    val result = expression.evaluate(env)
    println(result)
}

fun VarStmt.execute(env: Environment) {
    env.define(name.lexeme, initializer?.evaluate(env))
}

fun Stmt.execute(env: Environment) {
    when (this) {
        is PrintStmt -> execute(env)
        is ExpressionStmt -> execute(env)
        is VarStmt -> execute(env)
    }
}

fun Expr.evaluate(env: Environment): Any? {
    return when (this) {
        is AssignExpr -> evaluate(env)
        is TernaryExpr -> evaluate(env)
        is CommaExpr -> evaluate(env)
        is BinaryExpr -> evaluate(env)
        is UnaryExpr -> evaluate(env)
        is LiteralExpr -> value
        is VariableExpr -> evaluate(env)
        is GroupingExpr -> evaluate(env)
    }
}

fun AssignExpr.evaluate(env: Environment): Any? {
    val rhs = value.evaluate(env)
    env.assign(name, rhs)
    return rhs
}

fun TernaryExpr.evaluate(env: Environment): Any? {
    return if (condition.evaluate(env).isTruthy()) trueExpr.evaluate(env) else falseExpr.evaluate(env)
}

fun CommaExpr.evaluate(env: Environment): Any? {
    // evaluate all, return last expression's value
    var result: Any? = null
    for (expr in expressions) {
        result = expr.evaluate(env)
    }
    return result
}

fun BinaryExpr.evaluate(env: Environment): Any? {
    val left = left.evaluate(env)
    val right = right.evaluate(env)

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

fun GroupingExpr.evaluate(env: Environment): Any? {
    return expr.evaluate(env)
}

fun UnaryExpr.evaluate(env: Environment): Any? {
    val right = right.evaluate(env)

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

fun VariableExpr.evaluate(env: Environment): Any? {
    return env.get(name)
}

fun Any?.isTruthy(): Boolean {
    if (this == null) return false
    if (this is Boolean) return this

    return true
}