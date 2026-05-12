package com.example.profletterai.ui.screens.profiles

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.ui.theme.Indigo600

@Composable
fun ProfilesScreen(
    profiles: kotlinx.coroutines.flow.StateFlow<List<ProfessorProfile>>,
    onSave: (ProfessorProfile) -> Unit,
    onDelete: (ProfessorProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val list by profiles.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ProfessorProfile?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Professor Profiles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Save reusable recommender personas — pick one when you generate a letter.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            if (list.isEmpty()) {
                Text(
                    "No profiles yet. Tap “New profile” to add your first one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(list, key = { it.id }) { p ->
                        ProfileRow(p, onClick = { editing = p; showDialog = true }, onDelete = { onDelete(p) })
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
            onDismiss = { showDialog = false },
            onSave = { profile ->
                onSave(profile)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ProfileRow(
    profile: ProfessorProfile,
    onClick: () -> Unit,
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
                Text(profile.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${profile.recommenderName} • ${profile.recommenderInstitution}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (profile.notes.isNotBlank()) {
                    Text(
                        profile.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 2
                    )
                }
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
    onDismiss: () -> Unit,
    onSave: (ProfessorProfile) -> Unit
) {
    var displayName by remember { mutableStateOf(initial?.displayName ?: "") }
    var name by remember { mutableStateOf(initial?.recommenderName ?: "") }
    var institution by remember { mutableStateOf(initial?.recommenderInstitution ?: "") }
    var defaultContext by remember { mutableStateOf(initial?.defaultContext ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New profile" else "Edit profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Label") },
                    placeholder = { Text("e.g. Stanford CS — Dr Smith") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recommender name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Institution") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = defaultContext,
                    onValueChange = { defaultContext = it },
                    label = { Text("Default context") },
                    placeholder = { Text("How do you typically know your students?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                enabled = displayName.isNotBlank() && name.isNotBlank() && institution.isNotBlank(),
                onClick = {
                    onSave(
                        (initial ?: ProfessorProfile(
                            displayName = "", recommenderName = "", recommenderInstitution = ""
                        )).copy(
                            displayName = displayName.trim(),
                            recommenderName = name.trim(),
                            recommenderInstitution = institution.trim(),
                            defaultContext = defaultContext.trim(),
                            notes = notes.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) { Text(if (initial == null) "Create" else "Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
