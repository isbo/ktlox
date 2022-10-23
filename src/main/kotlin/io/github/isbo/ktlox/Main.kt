package io.github.isbo.ktlox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

var hadError = false

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
}

private fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    for (token in tokens) {
        println(token)
    }
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

fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] $where: $message")
    hadError = true
}