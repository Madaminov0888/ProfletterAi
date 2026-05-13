package com.example.profletterai.ui.screens.profiles

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.ui.components.MarkdownText
import com.example.profletterai.ui.theme.Indigo50
import com.example.profletterai.ui.theme.Indigo600
import com.example.profletterai.ui.theme.Indigo700
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfilesScreen(
    profiles: StateFlow<List<ProfessorProfile>>,
    isEnriching: StateFlow<Boolean>,
    enrichmentError: StateFlow<String?>,
    onSave: (ProfessorProfile) -> Unit,
    onDelete: (ProfessorProfile) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val list by profiles.collectAsState()
    val enriching by isEnriching.collectAsState()
    val error by enrichmentError.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ProfessorProfile?>(null) }
    var viewing by remember { mutableStateOf<ProfessorProfile?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Professor Profiles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Save reusable recommender personas. We'll run a profiling agent in the background to memorise their voice — pick a profile on Generate to skip that step.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            error?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp)
                        .clickable(onClick = onDismissError)
                ) {
                    Text(
                        text = "Enrichment: $err  (tap to dismiss)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            if (list.isEmpty()) {
                Text(
                    "No profiles yet. Tap \"New profile\" to add your first one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(list, key = { it.id }) { p ->
                        ProfileRow(
                            profile = p,
                            onClick = { viewing = p },
                            onEdit = { editing = p; showDialog = true },
                            onDelete = { onDelete(p) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { editing = null; showDialog = true },
            containerColor = Indigo600,
            contentColor = androidx.compose.ui.graphics.Color.White,
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("New profile") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (showDialog) {
        ProfileEditorDialog(
            initial = editing,
            isEnriching = enriching,
            onDismiss = { showDialog = false },
            onSave = { profile ->
                onSave(profile)
                showDialog = false
            }
        )
    }

    viewing?.let { p ->
        ProfileDetailDialog(profile = p, onDismiss = { viewing = null })
    }
}

@Composable
private fun ProfileRow(
    profile: ProfessorProfile,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (profile.enrichedProfile.isNotBlank()) {
                        Spacer(Modifier.size(6.dp))
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = "Enriched",
                            tint = Indigo600,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    "${profile.recommenderName} • ${profile.recommenderInstitution}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (profile.researchField.isNotBlank()) {
                    Text(
                        profile.researchField,
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo700,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (profile.notes.isNotBlank()) {
                    Text(
                        profile.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 2
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Indigo600)
            }
            IconButton(onClick = { confirmDelete = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete profile?") },
            text = { Text("This profile will no longer be available for quick-fill.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ProfileEditorDialog(
    initial: ProfessorProfile?,
    isEnriching: Boolean,
    onDismiss: () -> Unit,
    onSave: (ProfessorProfile) -> Unit
) {
    var displayName by remember { mutableStateOf(initial?.displayName ?: "") }
    var name by remember { mutableStateOf(initial?.recommenderName ?: "") }
    var institution by remember { mutableStateOf(initial?.recommenderInstitution ?: "") }
    var defaultContext by remember { mutableStateOf(initial?.defaultContext ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }


    val fieldsDirty = initial == null ||
        name.trim() != initial.recommenderName ||
        institution.trim() != initial.recommenderInstitution ||
        defaultContext.trim() != initial.defaultContext ||
        notes.trim() != initial.notes

    AlertDialog(
        onDismissRequest = { if (!isEnriching) onDismiss() },
        title = { Text(if (initial == null) "New profile" else "Edit profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    "When you save, we'll run a one-time AI pass that summarises this recommender's research area and voice. The cached result lets Generate skip the Recommender Profile step.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Label") },
                    placeholder = { Text("e.g. Stanford CS — Dr Smith") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isEnriching
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recommender name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isEnriching
                )
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Institution") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isEnriching
                )
                OutlinedTextField(
                    value = defaultContext,
                    onValueChange = { defaultContext = it },
                    label = { Text("Default context") },
                    placeholder = { Text("How do you typically know your students?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isEnriching
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isEnriching
                )
                if (isEnriching) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Indigo600
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            "Enriching profile…",
                            style = MaterialTheme.typography.bodySmall,
                            color = Indigo700
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isEnriching && displayName.isNotBlank() && name.isNotBlank() && institution.isNotBlank(),
                onClick = {
                    val base = initial ?: ProfessorProfile(
                        displayName = "", recommenderName = "", recommenderInstitution = ""
                    )
                    onSave(
                        base.copy(
                            displayName = displayName.trim(),
                            recommenderName = name.trim(),
                            recommenderInstitution = institution.trim(),
                            defaultContext = defaultContext.trim(),
                            notes = notes.trim(),

                            enrichedProfile = if (fieldsDirty) "" else base.enrichedProfile,
                            researchField = if (fieldsDirty) "" else base.researchField
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) { Text(if (initial == null) "Create + enrich" else "Save + enrich") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isEnriching) { Text("Cancel") }
        }
    )
}

@Composable
private fun ProfileDetailDialog(
    profile: ProfessorProfile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(profile.displayName) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    "${profile.recommenderName} • ${profile.recommenderInstitution}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (profile.researchField.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Indigo50)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            profile.researchField,
                            color = Indigo700,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (profile.defaultContext.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Default context", style = MaterialTheme.typography.labelMedium, color = Indigo700)
                    Text(profile.defaultContext, style = MaterialTheme.typography.bodySmall)
                }
                if (profile.enrichedProfile.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("AI-enriched profile", style = MaterialTheme.typography.labelMedium, color = Indigo700)
                    Spacer(Modifier.height(2.dp))
                    MarkdownText(markdown = profile.enrichedProfile)
                } else {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No enrichment cached yet — open Edit to re-run.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
