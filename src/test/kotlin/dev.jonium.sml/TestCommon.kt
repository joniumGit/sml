package dev.jonium.sml

import dev.jonium.sml.grammar.Null
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestCommon {

    @Test
    fun testNullValueIsNull() {
        assertNull(Null.value)
    }

    @Test
    fun testNullToStringIsToken() {
        assertEquals(".", Null.toString())
    }

}