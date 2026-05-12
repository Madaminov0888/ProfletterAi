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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.data.model.UserInput
import com.example.profletterai.ui.theme.Blue100
import com.example.profletterai.ui.theme.Blue50
import com.example.profletterai.ui.theme.Blue700
import com.example.profletterai.ui.theme.Indigo50
import com.example.profletterai.ui.theme.Indigo600
import com.example.profletterai.ui.theme.Indigo700

@Composable
fun InputFormScreen(
    initial: UserInput?,
    savedProfessors: List<ProfessorProfile>,
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

    fun applyProfile(p: ProfessorProfile) {
        recommenderName = p.recommenderName
        recommenderInstitution = p.recommenderInstitution
        if (recommenderContext.isBlank()) recommenderContext = p.defaultContext
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
                    SectionHeading("Quick-fill from saved professor profile")
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        savedProfessors.take(4).forEach { p ->
                            SavedProfileRow(profile = p, onApply = { applyProfile(p) })
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                SectionHeading("Recommender (You)")
                Spacer(Modifier.height(8.dp))
                FormField(label = "Full Name", value = recommenderName, onChange = { recommenderName = it }, placeholder = "e.g. Dr. Jane Smith")
                Spacer(Modifier.height(10.dp))
                FormField(label = "Institution / Organisation", value = recommenderInstitution, onChange = { recommenderInstitution = it }, placeholder = "e.g. Stanford University")

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
                                targetLinks = targetLinks
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
private fun SavedProfileRow(profile: ProfessorProfile, onApply: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Indigo50)
            .clickable(onClick = onApply)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Person, contentDescription = null, tint = Indigo700, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(8.dp))
        Column(Modifier.weight(1f)) {
            Text(profile.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "${profile.recommenderName} • ${profile.recommenderInstitution}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text("Use", color = Indigo700, style = MaterialTheme.typography.labelLarge)
    }
}
