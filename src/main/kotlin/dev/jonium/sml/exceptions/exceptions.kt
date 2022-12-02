package dev.jonium.sml.exceptions

import dev.jonium.sml.grammar.Word

class SMLParseError(
    message: String,
    line: Long,
    column: Long,
) : Exception("$message at [$line,$column]")

class SMLSyntaxError(
    message: String,
    line: Long,
    column: Long,
) : Exception("$message at [$line,$column]")

class SMLValueNotDefinedError(
    value: Word,
    line: Long,
    column: Long,
) : Exception("Value $value was not defined at [$line,$column]")

class SMLNullPointerError(
    line: Long,
    column: Long,
) : Exception("Null Pointer Exception at [$line,$column]")

class SMLTypeError(
    got: String,
    expected: String,
    line: Long,
    column: Long,
) : Exception("Type Error at [$line,$column]\nGot: ${got}Expected: $expected")