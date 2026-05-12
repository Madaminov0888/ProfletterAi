package com.example.profletterai.ui.screens.generate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.ui.components.MarkdownText
import com.example.profletterai.ui.theme.Green600
import com.example.profletterai.ui.theme.Indigo600

@Composable
fun DraftScreen(
    draft: String,
    savedRecommendationId: Long?,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(ViewMode.PREVIEW) }
    var localDraft by remember(draft) { mutableStateOf(draft) }

    LaunchedEffect(draft) { localDraft = draft }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Final Recommendation Letter",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onReset, shape = RoundedCornerShape(10.dp)) { Text("Start over") }
            OutlinedButton(
                onClick = { copyToClipboard(context, localDraft) },
                shape = RoundedCornerShape(10.dp)
            ) { Text("Copy") }
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (savedRecommendationId != null) Green600 else Indigo600
                )
            ) { Text(if (savedRecommendationId != null) "Saved ✓" else "Save to library") }
        }

        Spacer(Modifier.height(12.dp))

        // Mode toggle
        Row {
            TextButton(onClick = { viewMode = ViewMode.PREVIEW }) {
                Text("Preview", color = if (viewMode == ViewMode.PREVIEW) Indigo600 else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = { viewMode = ViewMode.EDIT }) {
                Text("Edit", color = if (viewMode == ViewMode.EDIT) Indigo600 else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                if (viewMode == ViewMode.EDIT) {
                    OutlinedTextField(
                        value = localDraft,
                        onValueChange = {
                            localDraft = it
                            onDraftChange(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 480.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    MarkdownText(markdown = localDraft)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("Recommendation Letter", text))
    Toast.makeText(context, "Letter copied to clipboard", Toast.LENGTH_SHORT).show()
}

private enum class ViewMode { PREVIEW, EDIT }
