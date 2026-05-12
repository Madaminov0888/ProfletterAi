package com.example.profletterai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.AgentState
import com.example.profletterai.data.model.AgentStatus
import com.example.profletterai.ui.theme.Green600
import com.example.profletterai.ui.theme.Indigo50
import com.example.profletterai.ui.theme.Indigo600
import com.example.profletterai.ui.theme.Red500
import com.example.profletterai.ui.theme.Slate200
import com.example.profletterai.ui.theme.Slate400

@Composable
fun AgentLoader(
    title: String,
    subtitle: String,
    statuses: List<AgentStatus>,
    modifier: Modifier = Modifier
) {
    val total = statuses.size.coerceAtLeast(1)
    val done = statuses.count { it.state == AgentState.DONE }
    val progress by animateFloatAsState(targetValue = done / total.toFloat(), label = "agentProgress")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        // Progress bar across the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Slate200)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .background(Indigo600)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Per-agent rows
        statuses.forEach { status ->
            AgentRow(status)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AgentRow(status: AgentStatus) {
    val containerColor = when (status.state) {
        AgentState.RUNNING -> Indigo50
        AgentState.DONE -> Color(0xFFF0FDF4)        // green-50
        AgentState.FAILED -> Color(0xFFFEF2F2)       // red-50
        AgentState.IDLE -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when (status.state) {
        AgentState.RUNNING -> Indigo600
        AgentState.DONE -> Green600
        AgentState.FAILED -> Red500
        AgentState.IDLE -> Slate200
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(
                text = status.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = status.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        StateIndicator(status.state)
    }
}

@Composable
private fun StateIndicator(state: AgentState) {
    Box(
        modifier = Modifier.size(28.dp).clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            AgentState.RUNNING -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Indigo600
            )
            AgentState.DONE -> Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(Green600),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
            }
            AgentState.FAILED -> Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(Red500),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Failed", tint = Color.White, modifier = Modifier.size(16.dp))
            }
            AgentState.IDLE -> Box(
                modifier = Modifier.size(10.dp).clip(CircleShape).background(Slate400)
            )
        }
    }
}
