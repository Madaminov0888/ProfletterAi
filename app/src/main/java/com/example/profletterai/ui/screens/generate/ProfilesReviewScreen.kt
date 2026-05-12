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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.Profiles
import com.example.profletterai.ui.components.MarkdownText
import com.example.profletterai.ui.theme.Indigo100
import com.example.profletterai.ui.theme.Indigo50
import com.example.profletterai.ui.theme.Indigo600
import com.example.profletterai.ui.theme.Indigo800

@Composable
fun ProfilesReviewScreen(
    profiles: Profiles,
    onUpdate: (Profiles) -> Unit,
    onProceed: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var local by remember(profiles) { mutableStateOf(profiles) }

    LaunchedEffect(profiles) { local = profiles }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Generated Intelligence Profiles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Review the agents' findings. Edit anything that's outdated.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!isEditing) {
                OutlinedButton(onClick = { isEditing = true }, shape = RoundedCornerShape(10.dp)) { Text("Edit") }
                OutlinedButton(onClick = onRetry, shape = RoundedCornerShape(10.dp)) { Text("Regenerate") }
                Button(
                    onClick = onProceed,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) { Text("Proceed to Planning") }
            } else {
                OutlinedButton(onClick = { local = profiles; isEditing = false }, shape = RoundedCornerShape(10.dp)) { Text("Cancel") }
                Button(
                    onClick = { onUpdate(local); isEditing = false },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Save changes") }
            }
        }

        Spacer(Modifier.height(16.dp))

        ProfileCard(
            title = "Recommender Profile",
            value = local.recommenderProfile,
            isEditing = isEditing,
            onChange = { local = local.copy(recommenderProfile = it) }
        )
        Spacer(Modifier.height(12.dp))
        ProfileCard(
            title = "Student Profile",
            value = local.studentProfile,
            isEditing = isEditing,
            onChange = { local = local.copy(studentProfile = it) }
        )
        Spacer(Modifier.height(12.dp))
        ProfileCard(
            title = "Target Profile",
            value = local.targetProfile,
            isEditing = isEditing,
            onChange = { local = local.copy(targetProfile = it) }
        )
        if (local.institutionalContext.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            ProfileCard(
                title = "Institutional Context",
                value = local.institutionalContext,
                isEditing = isEditing,
                onChange = { local = local.copy(institutionalContext = it) }
            )
        }

        Spacer(Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Indigo50),
            border = androidx.compose.foundation.BorderStroke(1.dp, Indigo100),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Strategic Overlap Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    color = Indigo800,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                if (isEditing) {
                    OutlinedTextField(
                        value = local.overlapAnalysis,
                        onValueChange = { local = local.copy(overlapAnalysis = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    MarkdownText(local.overlapAnalysis)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileCard(
    title: String,
    value: String,
    isEditing: Boolean,
    onChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = Indigo600,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            } else {
                MarkdownText(markdown = value)
            }
        }
    }
}
