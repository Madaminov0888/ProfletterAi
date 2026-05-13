package com.example.profletterai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.AppStep

@Composable
fun StepIndicator(
    current: AppStep,
    canGoToProfiles: Boolean,
    canGoToPlan: Boolean,
    canGoToDraft: Boolean,
    onNavigate: (AppStep) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepLabel("Input", isCurrent(current, AppStep.INPUT_DATA), enabled = true) {
            onNavigate(AppStep.INPUT_DATA)
        }
        Arrow()
        StepLabel(
            "Profiles",
            isCurrent(current, AppStep.BUILDING_PROFILES, AppStep.REVIEW_PROFILES),
            enabled = canGoToProfiles
        ) { onNavigate(AppStep.REVIEW_PROFILES) }
        Arrow()
        StepLabel(
            "Planning",
            isCurrent(current, AppStep.GENERATING_PLAN, AppStep.REVIEW_PLAN),
            enabled = canGoToPlan
        ) { onNavigate(AppStep.REVIEW_PLAN) }
        Arrow()
        StepLabel(
            "Draft",
            isCurrent(current, AppStep.GENERATING_DRAFT, AppStep.FINAL_DRAFT),
            enabled = canGoToDraft
        ) { onNavigate(AppStep.FINAL_DRAFT) }
    }
}

@Composable
private fun StepLabel(
    label: String,
    active: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val color = when {
        active -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.outline
    }
    Text(
        text = label,
        color = color,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .clickable(enabled = enabled, onClick = onClick)
    )
}

@Composable
private fun Arrow() {
    Text(
        text = "→",
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

private fun isCurrent(current: AppStep, vararg matches: AppStep): Boolean = matches.contains(current)
