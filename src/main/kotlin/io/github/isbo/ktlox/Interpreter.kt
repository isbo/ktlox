package io.github.isbo.ktlox

import io.github.isbo.ktlox.TokenType.*
import java.lang.ClassCastException

class Interpreter(printer: (message: Any?) -> Unit = ::println, replMode: Boolean = false) {
    private val context = RuntimeContext(Environment(), { message -> printer(message.toLoxString()) }, replMode)

    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements) {
                statement.execute(context)
            }
        } catch (e: RuntimeError) {
            runtimeError(e)
        }
    }
}

data class RuntimeContext(val env: Environment, val printer: (message: Any?) -> Unit, val replMode: Boolean)

fun ExpressionStmt.execute(ctxt: RuntimeContext) {
    val result = expression.evaluate(ctxt.env)
    if (ctxt.replMode) {
        ctxt.printer(result)
    }
}

fun PrintStmt.execute(ctxt: RuntimeContext) {
    val result = expression.evaluate(ctxt.env)
    ctxt.printer(result)
}

fun VarStmt.execute(ctxt: RuntimeContext) {
    ctxt.env.define(name.lexeme, initializer?.evaluate(ctxt.env))
}

fun BlockStmt.execute(ctxt: RuntimeContext) {
    val newCtxt = RuntimeContext(Environment(ctxt.env), ctxt.printer, ctxt.replMode)
    for (stmt in statements) {
        stmt.execute(newCtxt)
    }
}

fun Stmt.execute(ctxt: RuntimeContext) {
    when (this) {
        is PrintStmt -> execute(ctxt)
        is ExpressionStmt -> execute(ctxt)
        is VarStmt -> execute(ctxt)
        is BlockStmt -> execute(ctxt)
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

fun Any?.toLoxString(): String {
    return when(this) {
        null -> "nil"
        is Double -> {
            val str = toString()
            if (str.endsWith(".0")) str.slice(0..str.length-3) else str
        }
        else -> toString()
    }
}
