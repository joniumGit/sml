package dev.jonium.sml

import dev.jonium.sml.exceptions.SMLParseError
import dev.jonium.sml.utils.StringDataProvider
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TestParser {

    @Test
    fun testParsingDoesNotThrow() {
        val value = """
                    /* Simple test program with valid syntax */
                    if a @ ( 1 2 3 ) and k In (2 sum(1,2)) nand y ** 2 !| 0 then a = . / 0;
                    else do;
                        a = 2;
                        a = 3 nAnD y;
                    end /* comment */;
                    """.trimIndent()
        assertNotNull(Parser(StringDataProvider(value)).parse())
    }

    @Test
    fun testParsingTwiceThrows() {
        assertFailsWith<SMLParseError> {
            Parser(StringDataProvider("")).apply {
                parse()
                parse()
            }
        }
    }

}