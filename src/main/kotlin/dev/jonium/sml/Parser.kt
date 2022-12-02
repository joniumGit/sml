package dev.jonium.sml

import dev.jonium.sml.ast.AST
import dev.jonium.sml.exceptions.SMLParseError
import dev.jonium.sml.grammar.Token

class Parser(provider: DataProvider) {

    private val lexer = Lexer(provider)
    private val input = lexer.lex().iterator()

    private val tokenStack = ArrayDeque<ParseInput>()

    private fun hasNext() = tokenStack.isNotEmpty()

    private fun advance(): Token {
        if (input.hasNext()) tokenStack.addLast(input.next().let {
            ParseInput(it.first, it.second.first, it.second.second)
        })
        return tokenStack.removeFirstOrNull()?.token ?: throw SMLParseError(
            "Ran out of tokens to parse",
            lexer.line,
            lexer.column,
        )
    }

    private fun current(): Token = tokenStack[0].token

    private fun peek(n: Int = 1): Token? = tokenStack.getOrNull(n)?.token

    private fun parseTree() {
        while (hasNext()) advance()
    }

    fun parse(): AST {
        (0 until LOOKAHEAD).map {
            if (!input.hasNext()) throw SMLParseError(
                "Ran out of tokens to parse",
                lexer.line,
                lexer.column,
            )
            input.next().let {
                ParseInput(it.first, it.second.first, it.second.second)
            }
        }.forEach { tokenStack.addLast(it) }
        parseTree()
        return AST()
    }

    companion object {
        const val LOOKAHEAD = 1
    }

    private data class ParseInput(val token: Token, val line: Long, val column: Long)

}