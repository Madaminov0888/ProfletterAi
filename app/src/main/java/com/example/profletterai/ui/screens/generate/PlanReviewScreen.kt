package com.example.profletterai.ui.screens.generate

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.LetterPlan
import com.example.profletterai.ui.components.MarkdownText
import com.example.profletterai.ui.theme.Indigo600

@Composable
fun PlanReviewScreen(
    plan: LetterPlan,
    onRegenerate: (String) -> Unit,
    onProceed: (LetterPlan) -> Unit,
    onStructureEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var feedback by remember { mutableStateOf("") }
    var showReasoning by remember { mutableStateOf(false) }
    var isEditingStructure by remember { mutableStateOf(false) }
    var localStructure by remember(plan.structure) { mutableStateOf(plan.structure) }

    LaunchedEffect(plan.structure) { localStructure = plan.structure }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Drafting Strategy & Plan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))


        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Agent Reasoning", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { showReasoning = !showReasoning }) {
                        Text(if (showReasoning) "Hide" else "Show")
                    }
                }
                if (showReasoning) {
                    Text(
                        plan.reasoning,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))


        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Proposed Structure", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row {
                        TextButton(onClick = { isEditingStructure = false }) {
                            Text("Preview", color = if (!isEditingStructure) Indigo600 else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { isEditingStructure = true }) {
                            Text("Edit", color = if (isEditingStructure) Indigo600 else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (isEditingStructure) {
                    OutlinedTextField(
                        value = localStructure,
                        onValueChange = {
                            localStructure = it
                            onStructureEdit(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 280.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    MarkdownText(localStructure)
                }
            }
        }

        Spacer(Modifier.height(16.dp))


        Text(
            "Adjustment Instructions (optional)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = feedback,
            onValueChange = { feedback = it },
            placeholder = { Text("e.g. Emphasise her leadership in the final project...", style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            shape = RoundedCornerShape(10.dp)
        )
        Text(
            "Regenerate Plan rebuilds the outline from scratch using your feedback. Proceed to Draft uses the current outline as-is.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                enabled = feedback.isNotBlank(),
                onClick = { onRegenerate(feedback); feedback = "" },
                shape = RoundedCornerShape(10.dp)
            ) { Text("Regenerate Plan") }
            Button(
                onClick = { onProceed(plan.copy(structure = localStructure)) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) { Text("Proceed to Draft") }
        }
        Spacer(Modifier.height(24.dp))
    }
}
