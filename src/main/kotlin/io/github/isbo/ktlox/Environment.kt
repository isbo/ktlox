package io.github.isbo.ktlox

class Environment {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(token: Token): Any? {
        if (token.lexeme in values) return values[token.lexeme]

        throw RuntimeError(token, "Variable '${token.lexeme}' is not defined.")
    }
}