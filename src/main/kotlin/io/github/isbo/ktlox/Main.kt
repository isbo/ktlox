package io.github.isbo.ktlox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

var hadError = false
var hadRuntimeError = false

var interpreter: Interpreter? = null
fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: ktlox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        interpreter = Interpreter()
        runFile(args[0])
    } else {
        interpreter = Interpreter(replMode = true)
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
    val statements = parser.parse()
    if (hadError) {
        return
    }
    interpreter!!.interpret(statements)
}

private fun runPrompt() {
    while(true) {
        print("> ")
        val line = readLine() ?: break
        run(line)
        hadError = false
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