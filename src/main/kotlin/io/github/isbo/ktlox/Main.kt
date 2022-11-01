package io.github.isbo.ktlox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

var hadError = false
var hadRuntimeError = false

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: ktlox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

private fun runFile(fileName: String) {
    val content = Files.readString(Path.of(fileName), Charset.defaultCharset())
    run(content)
    if (hadError) exitProcess(65)
    if (hadRuntimeError) exitProcess(70)
}

private fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val expr = parser.parse()
    if (hadError) {
        return
    }
    print(expr!!.astPrinter())
    val interpreter = Interpreter()
    interpreter.interpret(expr)
}

private fun runPrompt() {
    while(true) {
        print("> ")
        val line = readLine() ?: break
        run(line)
        hadError = false
    }
}


fun Any?.toString(): String {
    return when(this) {
        null -> "nil"
        is Double -> {
            val str = toString()
            if (str.endsWith(".0")) str.slice(0..str.length-3) else str
        }
        else -> toString()
    }
}

fun error(line: Int, message: String) {
    report(line, "", message)
}
fun error(token: Token, message: String) {
    val where = if (token.type == TokenType.EOF) "end" else token.lexeme
    report(token.line, " at $where", message)
}
fun runtimeError(e: RuntimeError) {
    System.err.println("[line ${e.token.line}]: ${e.message}")
    hadRuntimeError = true
}
fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] $where: $message")
    hadError = true
}