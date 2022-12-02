package dev.jonium.sml.utils

import dev.jonium.sml.DataProvider

class StringDataProvider(private val text: String) : DataProvider {

    private var i = -1

    private fun get() = i.takeIf { it < text.length }?.let { text[it] }

    override fun next(): Char? = let { i += 1; get() }

    override fun peek(): Char? = next().also { i -= 1 }

}