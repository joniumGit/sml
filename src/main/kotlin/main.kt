import dev.jonium.sml.Lexer
import dev.jonium.sml.grammar.Separator
import dev.jonium.sml.utils.StringDataProvider

fun main() {
    val text = java.nio.file.Files.readString(java.nio.file.Path.of("samplefile.mlp"))

    val lines = text.lines()
    val maxLen = lines.size.toString().length
    println(lines.mapIndexed { index, s -> "${index.toString().padStart(maxLen)}: $s" }.joinToString(separator = "\n"))

    println(Lexer(StringDataProvider(text)).lex().toList().joinToString(separator = "") {
        " ${it.first}${it.takeIf { it.first == Separator.EOL }?.let { "\n" } ?: ""}"
    }.trimIndent())
}