package dev.jonium.sml.grammar

sealed interface Statement

interface Assignment : Statement {
    val target: Word
    val value: Expression
}

interface IfElse : Statement {
    val condition: Expression
    val ifBody: List<Statement>
    val elseBody: List<Statement>
}

sealed interface Expression {
    fun evaluate(): Primitive
}

interface ValueExpression : Expression {
    val target: Primitive
}

interface FunctionCall : Expression {
    val target: Word
    val args: List<Expression>
}

interface BinaryExpression : Expression {
    val lhs: Expression
    val op: Operator
    val rhs: Expression
}

interface UnaryExpression : Expression {
    val op: Operator
    val rhs: Expression
}