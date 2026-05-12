package com.example.profletterai.ui.screens.saved

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.example.profletterai.data.model.SavedRecommendation
import com.example.profletterai.service.export.LetterExporter
import com.example.profletterai.ui.components.MarkdownText
import com.example.profletterai.ui.theme.Indigo600
import java.text.DateFormat
import java.util.Date

@Composable
fun SavedRecommendationsScreen(
    items: kotlinx.coroutines.flow.StateFlow<List<SavedRecommendation>>,
    selected: kotlinx.coroutines.flow.StateFlow<SavedRecommendation?>,
    onSelect: (SavedRecommendation) -> Unit,
    onClearSelection: () -> Unit,
    onDelete: (SavedRecommendation) -> Unit,
    modifier: Modifier = Modifier
) {
    val list by items.collectAsState()
    val current by selected.collectAsState()

    if (current == null) {
        SavedListView(
            items = list,
            onSelect = onSelect,
            onDelete = onDelete,
            modifier = modifier
        )
    } else {
        SavedDetailView(
            item = current!!,
            onBack = onClearSelection,
            onDelete = { onDelete(current!!) },
            modifier = modifier
        )
    }
}

@Composable
private fun SavedListView(
    items: List<SavedRecommendation>,
    onSelect: (SavedRecommendation) -> Unit,
    onDelete: (SavedRecommendation) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Saved Recommendations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "${items.size} saved letters",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp), contentAlignment = Alignment.TopCenter) {
                Text(
                    "No saved letters yet. Generate one and tap Save to library.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { rec ->
                    SavedCard(rec, onSelect = { onSelect(rec) }, onDelete = { onDelete(rec) })
                }
            }
        }
    }
}

@Composable
private fun SavedCard(
    rec: SavedRecommendation,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(rec.studentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(rec.targetProgram, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "By ${rec.recommenderName} • ${formatDate(rec.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = { confirmDelete = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete this letter?") },
            text = { Text("This will permanently remove the saved recommendation.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SavedDetailView(
    item: SavedRecommendation,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                item.studentName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
        Text(
            "${item.targetProgram} • by ${item.recommenderName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        val exporter = remember(context) { LetterExporter(context) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { copyToClipboard(context, item.draftMarkdown) },
                shape = RoundedCornerShape(10.dp)
            ) { Text("Copy letter") }
            OutlinedButton(
                onClick = {
                    runCatching {
                        val title = "Recommendation Letter — ${item.studentName}"
                        val out = exporter.exportAndShare(title, item.draftMarkdown, LetterExporter.Format.DOCX)
                        Toast.makeText(context, "Saved to Downloads: ${out.displayName}", Toast.LENGTH_SHORT).show()
                        exporter.share(out)
                    }.onFailure {
                        Toast.makeText(context, "Export failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(6.dp))
                Text(".docx")
            }
            OutlinedButton(
                onClick = {
                    runCatching {
                        val title = "Recommendation Letter — ${item.studentName}"
                        val out = exporter.exportAndShare(title, item.draftMarkdown, LetterExporter.Format.PDF)
                        Toast.makeText(context, "Saved to Downloads: ${out.displayName}", Toast.LENGTH_SHORT).show()
                        exporter.share(out)
                    }.onFailure {
                        Toast.makeText(context, "Export failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(6.dp))
                Text(".pdf")
            }
        }

        Spacer(Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier
                .heightIn(min = 200.dp)
                .padding(14.dp)) {
                Text("Recommendation Letter", style = MaterialTheme.typography.titleMedium, color = Indigo600)
                Spacer(Modifier.height(6.dp))
                MarkdownText(item.draftMarkdown)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("Recommendation Letter", text))
    Toast.makeText(context, "Letter copied", Toast.LENGTH_SHORT).show()
}

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))
