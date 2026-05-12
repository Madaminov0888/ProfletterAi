package com.example.profletterai.ui.screens.generate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.profletterai.data.model.AppStep
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.ui.components.AgentLoader
import com.example.profletterai.ui.components.StepIndicator
import com.example.profletterai.ui.theme.Red50
import com.example.profletterai.ui.theme.Red700
import com.example.profletterai.ui.viewmodel.GenerationViewModel

@Composable
fun GenerateScreen(
    viewModel: GenerationViewModel,
    savedProfessors: List<ProfessorProfile>,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val statuses by viewModel.agentStatuses.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        StepIndicator(
            current = state.step,
            canGoToProfiles = state.profiles != null,
            canGoToPlan = state.plan != null,
            canGoToDraft = state.draft.isNotBlank(),
            onNavigate = viewModel::navigateToStep
        )

        state.error?.let { err ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Red50)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Error: $err",
                    color = Red700,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        when (state.step) {
            AppStep.INPUT_DATA -> InputFormScreen(
                initial = state.userInput,
                savedProfessors = savedProfessors,
                onSubmit = viewModel::submitInput
            )
            AppStep.BUILDING_PROFILES -> AgentLoader(
                title = "Building Intelligence Profiles",
                subtitle = "Five agents are researching the recommender, student and program in parallel...",
                statuses = statuses
            )
            AppStep.REVIEW_PROFILES -> state.profiles?.let { profiles ->
                ProfilesReviewScreen(
                    profiles = profiles,
                    onUpdate = viewModel::updateProfiles,
                    onProceed = viewModel::approveProfilesAndPlan,
                    onRetry = { viewModel.navigateToStep(AppStep.INPUT_DATA) }
                )
            }
            AppStep.GENERATING_PLAN -> CenteredMessage(
                title = "Thinking about strategy",
                subtitle = "The AI is reasoning through the best structure for your letter..."
            )
            AppStep.REVIEW_PLAN -> state.plan?.let { plan ->
                PlanReviewScreen(
                    plan = plan,
                    onRegenerate = viewModel::regeneratePlan,
                    onProceed = viewModel::proceedToDraft,
                    onStructureEdit = viewModel::updatePlanStructure
                )
            }
            AppStep.GENERATING_DRAFT -> CenteredMessage(
                title = "Writing comprehensive draft",
                subtitle = "Synthesising plan, profiles and persona into a formal letter..."
            )
            AppStep.FINAL_DRAFT -> DraftScreen(
                draft = state.draft,
                savedRecommendationId = state.savedRecommendationId,
                onDraftChange = viewModel::updateDraft,
                onSave = viewModel::saveCurrentDraft,
                onReset = viewModel::reset
            )
        }
    }
}

@Composable
private fun CenteredMessage(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        androidx.compose.material3.CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
