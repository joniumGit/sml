package dev.jonium.sml.grammar

interface Token

enum class Operator(val token: String) : Token {
    // Special
    IN("@"),

    // Boolean
    EQ("="),
    NE("!="),
    NOT("!"),
    AND("&"),
    OR("|"),
    NOR("!|"),
    NAND("!&"),

    // Numeric
    PLUS("+"),
    MINUS("-"),
    DIV("/"),
    MUL("*"),
    MOD("%"),
    POW("**");

    companion object {
        private val members = values().associateBy { it.name }
        private val tokens = values().associateBy { it.token }
        fun lookup(value: String) = members[value.uppercase()] ?: tokens[value]
    }

}

enum class Keyword : Token {
    // General
    IN,

    // Conditional logic
    IF,
    THEN,
    ELSE,

    // Context
    DO,
    END;

    companion object {
        private val members = values().associateBy { it.name }
        fun lookup(value: String) = members[value.uppercase()]
    }

}

enum class Separator(val token: String) : Token {
    LBRACE("("),
    RBRACE(")"),
    COMMA(","),
    EOL(";");

    companion object {
        private val members = values().associateBy { it.token }
        private val tokens = values().associateBy { it.token }
        fun lookup(value: String) = members[value.uppercase()] ?: tokens[value]
    }

}

data class Word(val value: String) : Token

sealed interface Primitive : Token {
    val value: Any?
}

object Null : Primitive {
    override val value = null
    override fun toString() = "."
}

sealed interface Literal : Primitive

sealed interface NumericLiteral : Literal

data class IntLiteral(override val value: Long) : NumericLiteral

data class FloatLiteral(override val value: Double) : NumericLiteral

data class StringLiteral(override val value: String) : Literal
