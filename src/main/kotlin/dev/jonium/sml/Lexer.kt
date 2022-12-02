package dev.jonium.sml

import dev.jonium.sml.exceptions.SMLParseError
import dev.jonium.sml.grammar.*

class Lexer(private val provider: DataProvider) {

    internal var line = 0L
    internal var column = -1L

    private fun fail(message: String): Nothing = throw SMLParseError(message, line, column)

    private fun peek(): Char = provider.peek() ?: ' '

    private fun nextUnsafe(): Char? = provider.next()?.also {
        if (it == '\n') {
            line += 1
            column = -1
        } else {
            column += 1
        }
    }

    private fun next(): Char = nextUnsafe() ?: fail("Encountered EOF too early")

    private fun string(start: Char): Token? = start.takeIf { it == '\'' || it == '"' }?.let {
        var buffer = ""

        var c = next()
        while (c != start || c == peek()) {
            buffer += c
            c = next()
        }

        buffer.replace("" + start + start, "" + start) // replace "" with " or '' with '

        StringLiteral(buffer)
    }

    private fun isNextValidSeparatorForDigit(): Boolean = peek().let { nextChar ->
        nextChar.isWhitespace()
                || Operator.lookup(nextChar.toString()) != null
                || Separator.lookup(nextChar.toString())?.let { it != Separator.LBRACE } == true
    }

    private fun parseFloat(previousValue: String): FloatLiteral {
        var buffer = previousValue
        buffer += next()

        while (peek().isDigit()) {
            buffer += next()
        }

        if (peek().uppercase() == "E") {
            buffer += next()

            peek().let { nextChar ->
                if (nextChar != '-' && nextChar != '+' && !nextChar.isDigit()) fail("Encountered invalid float exponent sign")
                buffer += next()
            }

            if (!peek().isDigit()) fail("Encountered invalid float exponent")

            while (peek().isDigit()) {
                buffer += next()
            }
        }

        if (!isNextValidSeparatorForDigit()) {
            fail("Encountered invalid float literal")
        }

        return FloatLiteral(value = buffer.toDouble())
    }

    private fun digit(start: Char): Token? = start.takeIf { it.isDigit() }?.let {
        var buffer = "" + it

        while (peek().isDigit()) {
            buffer += next()
        }

        val separator = peek()

        when {
            separator == '.' -> parseFloat(buffer)

            isNextValidSeparatorForDigit() -> IntLiteral(value = buffer.toLong())

            else -> fail("Encountered invalid int literal")
        }
    }

    private fun word(start: Char): Token? = start.takeIf { it.isLetter() || it == '_' }?.let { c ->
        var buffer = "" + c

        while (peek().let { it.isLetterOrDigit() || it == '_' }) buffer += next()

        if (buffer.all { it == '_' }) fail("Encountered invalid word")

        return Keyword.lookup(buffer)
            ?: Operator.lookup(buffer)
            ?: Word(buffer)

    }

    private fun token(c: Char): Token? {
        val maybeToken = c.toString()
        return if (c == '.') Null else
            Operator.lookup(maybeToken).let { Operator.lookup(maybeToken + peek())?.also { next() } ?: it }
                ?: Separator.lookup(maybeToken)

    }

    private fun consumeSpace(lastChar: Char?): Char? {
        var c: Char? = lastChar
        while (c != null && c.isWhitespace()) c = nextUnsafe()
        return c
    }

    private fun blockComment(start: Char): Boolean = start.takeIf { it == '/' && peek() == '*' }?.let {
        next() // consume *
        var c = next()
        while (c != '*' || peek() != '/') c = next()
        next() // consume /
        true
    } ?: false

    private fun nextChar(): Char? {
        var c = consumeSpace(nextUnsafe())
        while (c != null && blockComment(c)) c = consumeSpace(nextUnsafe())
        return c
    }

    private fun lexToken(c: Char) = token(c)
        ?: digit(c)
        ?: string(c)
        ?: word(c)
        ?: fail("Encountered unknown token $c")

    fun lex(): Sequence<Pair<Token, Pair<Long, Long>>> {
        return generateSequence {
            val c = nextChar()
            c.takeIf { it != null }?.let {
                val finalLine: Long = line
                val finalCol: Long = column
                lexToken(it) to (finalLine to finalCol)
            }
        }
    }

}
