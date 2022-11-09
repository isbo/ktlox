package io.github.isbo.ktlox

class Environment(private val enclosingEnv: Environment? = null) {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(name: Token): Any? {
        if (name.lexeme in values) return values[name.lexeme]

        if (enclosingEnv != null) return enclosingEnv.get(name)

        throw RuntimeError(name, "Variable '${name.lexeme}' is not defined.")
    }

    fun assign(name: Token, value: Any?) {
        if (name.lexeme in values) {
            define(name.lexeme, value)
            return
        }
        enclosingEnv?.assign(name, value)

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }
}