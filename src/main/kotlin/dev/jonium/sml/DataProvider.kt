package dev.jonium.sml

interface DataProvider {

    fun next(): Char?
    fun peek(): Char?

}