package com.example.profletterai.ui.screens.generate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.profletterai.data.model.UserInput
import com.example.profletterai.ui.theme.Blue100
import com.example.profletterai.ui.theme.Blue50
import com.example.profletterai.ui.theme.Blue700
import com.example.profletterai.ui.theme.Green600
import com.example.profletterai.ui.theme.Indigo50
import com.example.profletterai.ui.theme.Indigo600
import com.example.profletterai.ui.theme.Indigo700

@Composable
fun InputFormScreen(
    initial: UserInput?,
    savedProfessors: List<ProfessorProfile>,
    savedLettersCount: Int,
    onNavigateToProfiles: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onSubmit: (UserInput) -> Unit,
    modifier: Modifier = Modifier
) {
    var recommenderName by remember { mutableStateOf(initial?.recommenderName ?: "") }
    var recommenderInstitution by remember { mutableStateOf(initial?.recommenderInstitution ?: "") }
    var recommenderContext by remember { mutableStateOf(initial?.recommenderContext ?: "") }
    var studentName by remember { mutableStateOf(initial?.studentName ?: "") }
    var targetProgram by remember { mutableStateOf(initial?.targetProgram ?: "") }
    var studentResume by remember { mutableStateOf(initial?.studentResume ?: "") }
    var linkInput by remember { mutableStateOf("") }
    var targetLinks by remember { mutableStateOf(initial?.targetLinks ?: emptyList()) }
    // Which saved profile (if any) the user picked. Null means "fill manually".
    var selectedProfileId by remember { mutableStateOf<Long?>(null) }
    val selectedProfile = savedProfessors.firstOrNull { it.id == selectedProfileId }

    fun applyProfile(p: ProfessorProfile) {
        selectedProfileId = p.id
        recommenderName = p.recommenderName
        recommenderInstitution = p.recommenderInstitution
        if (recommenderContext.isBlank()) recommenderContext = p.defaultContext
    }

    fun clearProfileSelection() {
        selectedProfileId = null
        // Don't wipe the text fields — the user might still want them.
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Shortcut cards (top of form): existing profiles + saved letters
        if (savedProfessors.isNotEmpty() || savedLettersCount > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (savedProfessors.isNotEmpty()) {
                    ShortcutCard(
                        icon = Icons.Filled.Person,
                        label = "${savedProfessors.size} profile${if (savedProfessors.size == 1) "" else "s"}",
                        sublabel = "Tap to manage",
                        onClick = onNavigateToProfiles,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (savedLettersCount > 0) {
                    ShortcutCard(
                        icon = Icons.Filled.Bookmark,
                        label = "$savedLettersCount saved letter${if (savedLettersCount == 1) "" else "s"}",
                        sublabel = "Open library",
                        onClick = onNavigateToSaved,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Let's start gathering information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (savedProfessors.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    SectionHeading("Use a saved professor profile (optional)")
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        savedProfessors.take(5).forEach { p ->
                            SavedProfileRow(
                                profile = p,
                                selected = p.id == selectedProfileId,
                                onApply = {
                                    if (p.id == selectedProfileId) clearProfileSelection()
                                    else applyProfile(p)
                                }
                            )
                        }
                    }
                    if (selectedProfile != null) {
                        Spacer(Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Indigo50)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, tint = Indigo700, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(
                                "Using ${selectedProfile.displayName} — the Recommender Profile step will be skipped.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Indigo700,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Clear",
                                color = Indigo700,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable(onClick = ::clearProfileSelection)
                            )
                        }
                    } else {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Or skip and fill in the recommender fields below manually.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                SectionHeading("Recommender (You)")
                Spacer(Modifier.height(8.dp))
                FormField(label = "Full Name", value = recommenderName, onChange = { recommenderName = it; if (selectedProfile != null && it != selectedProfile.recommenderName) clearProfileSelection() }, placeholder = "e.g. Dr. Jane Smith")
                Spacer(Modifier.height(10.dp))
                FormField(label = "Institution / Organisation", value = recommenderInstitution, onChange = { recommenderInstitution = it; if (selectedProfile != null && it != selectedProfile.recommenderInstitution) clearProfileSelection() }, placeholder = "e.g. Stanford University")

                Spacer(Modifier.height(16.dp))
                SectionHeading("The Student")
                Spacer(Modifier.height(8.dp))
                FormField(label = "Student Name", value = studentName, onChange = { studentName = it }, placeholder = "e.g. John Doe")
                Spacer(Modifier.height(10.dp))
                FormField(label = "Target Program / Job", value = targetProgram, onChange = { targetProgram = it }, placeholder = "e.g. MIT PhD in Computer Science")

                Spacer(Modifier.height(16.dp))
                FormField(
                    label = "How do you know the student? (Context)",
                    value = recommenderContext,
                    onChange = { recommenderContext = it },
                    placeholder = "e.g. She was in my Advanced Algorithms class and received an A...",
                    minLines = 3
                )

                Spacer(Modifier.height(16.dp))
                FormField(
                    label = "Student background (paste resume / experience)",
                    value = studentResume,
                    onChange = { studentResume = it },
                    placeholder = "Paste relevant experience, grades, projects here...",
                    minLines = 5
                )

                Spacer(Modifier.height(16.dp))
                SectionHeading("Target links (optional)")
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        placeholder = { Text("https://program-page.example/details") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    OutlinedButton(
                        onClick = {
                            val trimmed = linkInput.trim()
                            if (trimmed.isNotBlank()) {
                                targetLinks = targetLinks + trimmed
                                linkInput = ""
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Add") }
                }

                if (targetLinks.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        targetLinks.forEachIndexed { i, link ->
                            LinkChip(link = link, onRemove = {
                                targetLinks = targetLinks.toMutableList().apply { removeAt(i) }
                            })
                        }
                    }
                }
                Text(
                    text = "Our AI agents will use these links to tailor the letter.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        onSubmit(
                            UserInput(
                                recommenderName = recommenderName.trim(),
                                recommenderInstitution = recommenderInstitution.trim(),
                                recommenderContext = recommenderContext.trim(),
                                studentName = studentName.trim(),
                                studentResume = studentResume.trim(),
                                targetProgram = targetProgram.trim(),
                                targetLinks = targetLinks,
                                cachedRecommenderProfile = selectedProfile?.enrichedProfile?.takeIf { it.isNotBlank() }
                            )
                        )
                    },
                    enabled = listOf(recommenderName, recommenderInstitution, recommenderContext, studentName, targetProgram).all { it.isNotBlank() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Generate Comprehensive Profiles", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShortcutCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    sublabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Indigo50)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Indigo700, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(8.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Indigo700)
            Text(sublabel, style = MaterialTheme.typography.labelSmall, color = Indigo700)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Indigo700, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Indigo600,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun LinkChip(link: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Blue50)
            .border(1.dp, Blue100, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = link,
            style = MaterialTheme.typography.bodySmall,
            color = Blue700,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.Close,
            contentDescription = "Remove",
            tint = Blue700,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onRemove)
        )
    }
}

@Composable
private fun SavedProfileRow(
    profile: ProfessorProfile,
    selected: Boolean,
    onApply: () -> Unit
) {
    val containerColor = if (selected) Indigo600 else Indigo50
    val textColor = if (selected) androidx.compose.ui.graphics.Color.White else Indigo700
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .clickable(onClick = onApply)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Person, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(8.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    profile.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (profile.enrichedProfile.isNotBlank()) {
                    Spacer(Modifier.size(6.dp))
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = "Cached enrichment",
                        tint = if (selected) androidx.compose.ui.graphics.Color.White else Green600,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Text(
                "${profile.recommenderName} • ${profile.recommenderInstitution}",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
        Text(if (selected) "Selected ✓" else "Use", color = textColor, style = MaterialTheme.typography.labelLarge)
    }
}
