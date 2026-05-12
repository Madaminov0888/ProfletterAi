package com.example.profletterai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Lightweight Markdown renderer — handles headers (#, ##, ###), bullet/numbered
 * lists, **bold**, *italic*, `inline code`, and paragraphs. It is intentionally
 * minimal so we don't need an extra library.
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val lines = markdown.lines()
    Column(modifier = modifier) {
        var i = 0
        while (i < lines.size) {
            val raw = lines[i]
            val line = raw.trimEnd()
            when {
                line.startsWith("### ") -> {
                    Text(
                        text = parseInline(line.removePrefix("### ")),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                }
                line.startsWith("## ") -> {
                    Text(
                        text = parseInline(line.removePrefix("## ")),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                }
                line.startsWith("# ") -> {
                    Text(
                        text = parseInline(line.removePrefix("# ")),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                }
                line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") -> {
                    val depth = (line.length - line.trimStart().length) / 2
                    val content = line.trimStart().drop(2)
                    Text(
                        text = parseInline("• $content"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = (12 * depth).dp + 4.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Regex("^\\s*\\d+\\.\\s").containsMatchIn(line) -> {
                    Text(
                        text = parseInline(line.trimStart()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                }
                line.isBlank() -> {
                    Spacer(Modifier.height(8.dp))
                }
                else -> {
                    Text(
                        text = parseInline(line),
                        style = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
            i++
        }
    }
}

/**
 * Parse **bold**, *italic* and `code` into an AnnotatedString.
 * Greedy left-to-right tokenizer — good enough for our agent output.
 */
private fun parseInline(text: String): AnnotatedString = buildAnnotatedString {
    var idx = 0
    while (idx < text.length) {
        when {
            text.startsWith("**", idx) -> {
                val end = text.indexOf("**", idx + 2)
                if (end == -1) { append(text.substring(idx)); break }
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(text.substring(idx + 2, end))
                }
                idx = end + 2
            }
            text[idx] == '*' && idx + 1 < text.length && text[idx + 1] != ' ' -> {
                val end = text.indexOf('*', idx + 1)
                if (end == -1) { append(text.substring(idx)); break }
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(text.substring(idx + 1, end))
                }
                idx = end + 1
            }
            text[idx] == '`' -> {
                val end = text.indexOf('`', idx + 1)
                if (end == -1) { append(text.substring(idx)); break }
                withStyle(
                    SpanStyle(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        background = androidx.compose.ui.graphics.Color(0xFFF1F5F9)
                    )
                ) { append(text.substring(idx + 1, end)) }
                idx = end + 1
            }
            else -> { append(text[idx]); idx++ }
        }
    }
}
