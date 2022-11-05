package io.github.isbo.ktlox

import java.lang.RuntimeException

class RuntimeError(val token: Token, override val message: String) : RuntimeException()