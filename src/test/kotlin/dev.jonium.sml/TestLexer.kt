package dev.jonium.sml

import dev.jonium.sml.exceptions.SMLParseError
import dev.jonium.sml.grammar.*
import dev.jonium.sml.utils.StringDataProvider
import kotlin.test.*

class TestLexer {

    @Test
    fun testSpaceIsNotLetterOrDigit() {
        assertFalse(' '.isLetterOrDigit())
    }

    @Test
    fun testLexInt() {
        val value = 12345L
        assertEquals(
            IntLiteral(value),
            Lexer(StringDataProvider(value.toString())).lex().first().first,
        )
    }

    @Test
    fun testLexIntFailsOnLetterContinue() {
        val value = "11a"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexIntFailsOnBrace() {
        val value = "11("
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloat() {
        val value = 11.15
        assertEquals(
            FloatLiteral(value).toString(),
            Lexer(StringDataProvider(value.toString())).lex().first().first.toString(),
        )
    }

    @Test
    fun testLexFloatScientificSmall() {
        val value = "11.15e-50"
        assertEquals(
            FloatLiteral(value.toDouble()).toString(),
            Lexer(StringDataProvider(value)).lex().first().first.toString(),
        )
    }

    @Test
    fun testLexFloatScientificSmallKotlinLiteral() {
        val value = 11.15e-50
        assertEquals(
            FloatLiteral(value).toString(),
            Lexer(StringDataProvider(value.toString())).lex().first().first.toString(),
        )
    }

    @Test
    fun testLexFloatScientificBigKotlinLiteral() {
        val value = 11.15e+50
        assertEquals(
            FloatLiteral(value).toString(),
            Lexer(StringDataProvider(value.toString())).lex().first().first.toString(),
        )
    }

    @Test
    fun testLexFloatScientificLarge() {
        val value = "11.15E+50"
        assertEquals(
            FloatLiteral(value.toDouble()).toString(),
            Lexer(StringDataProvider(value)).lex().first().first.toString(),
        )
    }

    @Test
    fun testLexFloatFailsOnMissingExponent() {
        val value = "11.125E"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnInvalidSign() {
        val value = "11.125e="
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnInvalidSignOkExponent() {
        val value = "11.125e=12"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnInvalidSignOkExponentErrorLocation() {
        val value = "\n\n11.125e=12"
        assertFailsWith<SMLParseError>("Encountered invalid float exponent sign at [2,7]") {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnInvalidExponent() {
        val value = "11.125e-a"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnDoubleDot() {
        val value = "11.125."
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnLetterContinue() {
        val value = "11.125a"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexFloatFailsOnBrace() {
        val value = "11.125("
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexString() {
        val value = "'abcd'"
        assertEquals(
            StringLiteral(value.trim('\'')),
            Lexer(StringDataProvider(value)).lex().first().first,
        )
    }

    @Test
    fun testLexStringFailsOnMissingEnd() {
        val value = "'abcd"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexStringFailsOnMissingStart() {
        val value = "abcd'"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexStringFailsOnMissingStartEofMessage() {
        val value = "abcd'"
        assertFailsWith<SMLParseError>("Encountered EOF too early") {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexValue() {
        val value = "a"
        assertEquals(
            Word(value),
            Lexer(StringDataProvider(value)).lex().first().first,
        )
    }

    @Test
    fun testLexWord() {
        val value = "abcd"
        assertEquals(
            Word(value),
            Lexer(StringDataProvider(value)).lex().first().first,
        )
    }

    @Test
    fun testLexAssignment() {
        val value = "a = b"
        assertEquals(
            listOf(
                Word("a"),
                Operator.EQ,
                Word("b"),
            ),
            Lexer(StringDataProvider(value)).lex().map { it.first }.toList(),
        )
    }

    @Test
    fun testLexAssignmentWithCommentInBeginning() {
        val value = "/* comment */a = b"
        assertEquals(
            listOf(
                Word("a"),
                Operator.EQ,
                Word("b"),
            ),
            Lexer(StringDataProvider(value)).lex().map { it.first }.toList(),
        )
    }

    @Test
    fun testLexAssignmentWithCommentsInMiddle() {
        val value = "a/* comment */= /* comment */ b"
        assertEquals(
            listOf(
                Word("a"),
                Operator.EQ,
                Word("b"),
            ),
            Lexer(StringDataProvider(value)).lex().map { it.first }.toList(),
        )
    }

    @Test
    fun testLexAssignmentWithCommentsAtEnd() {
        val value = "a=b/* comment */"
        assertEquals(
            listOf(
                Word("a"),
                Operator.EQ,
                Word("b"),
            ),
            Lexer(StringDataProvider(value)).lex().map { it.first }.toList(),
        )
    }

    @Test
    fun testLexComment() {
        val value = "/* abcd */"
        assertTrue(
            Lexer(StringDataProvider(value)).lex().toList().isEmpty()
        )
    }

    @Test
    fun testLexCommentWithSlash() {
        val value = "/* ab/cd */"
        assertTrue(
            Lexer(StringDataProvider(value)).lex().toList().isEmpty()
        )
    }

    @Test
    fun testLexCommentWithStar() {
        val value = "/* ab*cd */"
        assertTrue(
            Lexer(StringDataProvider(value)).lex().toList().isEmpty()
        )
    }

    @Test
    fun testLexFailsOnBadToken() {
        val value = "\\\\"
        assertFailsWith(SMLParseError::class) {
            Lexer(StringDataProvider(value)).lex().toList()
        }
    }

    @Test
    fun testLexCoordinates() {
        val value = "a\n b"
        assertEquals(
            Lexer(StringDataProvider(value)).lex().map { it.second }.toList(),
            listOf(0L to 0L, 1L to 1L)
        )
    }

    @Test
    fun testLexComplexStatement() {
        val value = """
                    /* Simple test program with invalid syntax */
                    if a @ ( 1 2,3 ) and k In (2 sum(1,2)) nand y ** 2 !| 0 then . = Null / 0
                    else do;
                        a = 2
                        a = 3 nAnD y
                    end /* comment */;
                    """.trimIndent()
        assertEquals(
            listOf(
                // IF
                Keyword.IF,
                // IN
                Word("a"),
                Operator.IN,
                Separator.LBRACE,
                IntLiteral(1),
                IntLiteral(2),
                Separator.COMMA,
                IntLiteral(3),
                Separator.RBRACE,
                // AND
                Operator.AND,
                // IN
                Word("k"),
                Operator.IN,
                Separator.LBRACE,
                IntLiteral(2),
                Word("sum"),
                Separator.LBRACE,
                IntLiteral(1),
                Separator.COMMA,
                IntLiteral(2),
                Separator.RBRACE,
                Separator.RBRACE,
                // NAND
                Operator.NAND,
                // COND
                Word("y"),
                Operator.POW,
                IntLiteral(2),
                Operator.NOR,
                IntLiteral(0),
                // THEN
                Keyword.THEN,
                Null,
                Operator.EQ,
                Word("Null"),
                Operator.DIV,
                IntLiteral(0),
                // ELSE
                Keyword.ELSE,
                Keyword.DO,
                Separator.EOL,
                // STATEMENT
                Word("a"),
                Operator.EQ,
                IntLiteral(2),
                // STATEMENT
                Word("a"),
                Operator.EQ,
                IntLiteral(3),
                Operator.NAND,
                Word("y"),
                // END
                Keyword.END,
                Separator.EOL,
            ),
            Lexer(StringDataProvider(value)).lex().map { it.first }.toList(),
        )
    }

}