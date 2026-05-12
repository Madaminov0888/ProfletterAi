package com.example.profletterai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.profletterai.data.model.AgentStatus
import com.example.profletterai.data.model.AppStep
import com.example.profletterai.data.model.LetterPlan
import com.example.profletterai.data.model.Profiles
import com.example.profletterai.data.model.UserInput
import com.example.profletterai.repository.RecommendationRepository
import com.example.profletterai.service.AgentOrchestrator
import com.example.profletterai.service.DraftService
import com.example.profletterai.service.PlanService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Holds the wizard state for the Generate tab.
 *
 * The ViewModel only:
 *   - holds UI state
 *   - calls into services
 *   - exposes flows
 *
 * No prompt construction, no HTTP, no JSON parsing — those live in the
 * service layer, exactly as the user asked.
 */
class GenerationViewModel(
    private val orchestrator: AgentOrchestrator,
    private val planService: PlanService,
    private val draftService: DraftService,
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    data class UiState(
        val step: AppStep = AppStep.INPUT_DATA,
        val userInput: UserInput? = null,
        val profiles: Profiles? = null,
        val plan: LetterPlan? = null,
        val draft: String = "",
        val error: String? = null,
        val savedRecommendationId: Long? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    /** Live agent statuses while the orchestrator is running. */
    val agentStatuses: StateFlow<List<AgentStatus>> = orchestrator.statuses

    // ── Step 1 → 2: research
    fun submitInput(input: UserInput) {
        _state.value = _state.value.copy(
            userInput = input,
            step = AppStep.BUILDING_PROFILES,
            error = null,
            savedRecommendationId = null
        )
        viewModelScope.launch {
            runCatching { orchestrator.buildProfiles(input) }
                .onSuccess { profiles ->
                    _state.value = _state.value.copy(
                        profiles = profiles,
                        step = AppStep.REVIEW_PROFILES
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to build profiles.",
                        step = AppStep.INPUT_DATA
                    )
                }
        }
    }

    fun updateProfiles(updated: Profiles) {
        _state.value = _state.value.copy(profiles = updated)
    }

    // ── Step 2 → 3: plan
    fun approveProfilesAndPlan() {
        val s = _state.value
        val input = s.userInput ?: return
        val profiles = s.profiles ?: return
        _state.value = s.copy(step = AppStep.GENERATING_PLAN, error = null)
        viewModelScope.launch {
            runCatching { planService.generatePlan(input, profiles) }
                .onSuccess { plan ->
                    _state.value = _state.value.copy(plan = plan, step = AppStep.REVIEW_PLAN)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to generate plan.",
                        step = AppStep.REVIEW_PROFILES
                    )
                }
        }
    }

    fun regeneratePlan(feedback: String) {
        val s = _state.value
        val input = s.userInput ?: return
        val profiles = s.profiles ?: return
        val previous = s.plan
        _state.value = s.copy(step = AppStep.GENERATING_PLAN, error = null)
        viewModelScope.launch {
            runCatching { planService.generatePlan(input, profiles, previous, feedback) }
                .onSuccess { plan ->
                    _state.value = _state.value.copy(plan = plan, step = AppStep.REVIEW_PLAN)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to regenerate plan.",
                        step = AppStep.REVIEW_PLAN
                    )
                }
        }
    }

    fun updatePlanStructure(newStructure: String) {
        val plan = _state.value.plan ?: return
        _state.value = _state.value.copy(plan = plan.copy(structure = newStructure))
    }

    // ── Step 3 → 4: draft
    fun proceedToDraft(finalPlan: LetterPlan) {
        val s = _state.value
        val input = s.userInput ?: return
        val profiles = s.profiles ?: return
        _state.value = s.copy(plan = finalPlan, step = AppStep.GENERATING_DRAFT, error = null)
        viewModelScope.launch {
            runCatching { draftService.generateDraft(input, profiles, finalPlan) }
                .onSuccess { draft ->
                    _state.value = _state.value.copy(draft = draft, step = AppStep.FINAL_DRAFT)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to generate draft.",
                        step = AppStep.REVIEW_PLAN
                    )
                }
        }
    }

    fun updateDraft(newDraft: String) {
        _state.value = _state.value.copy(draft = newDraft)
    }

    fun rewriteSelection(selection: String, instruction: String, onResult: (String) -> Unit) {
        val draft = _state.value.draft
        viewModelScope.launch {
            runCatching { draftService.rewriteSelection(selection, instruction, draft) }
                .onSuccess(onResult)
                .onFailure { e ->
                    _state.value = _state.value.copy(error = e.message ?: "Failed to rewrite.")
                }
        }
    }

    fun saveCurrentDraft() {
        val s = _state.value
        val input = s.userInput ?: return
        val profiles = s.profiles ?: return
        val plan = s.plan ?: return
        if (s.draft.isBlank()) return

        viewModelScope.launch {
            val id = recommendationRepository.save(input, profiles, plan, s.draft)
            _state.value = _state.value.copy(savedRecommendationId = id)
        }
    }

    fun navigateToStep(step: AppStep) {
        val s = _state.value
        val canNavigate = when (step) {
            AppStep.INPUT_DATA -> true
            AppStep.REVIEW_PROFILES, AppStep.BUILDING_PROFILES -> s.profiles != null
            AppStep.REVIEW_PLAN, AppStep.GENERATING_PLAN -> s.plan != null
            AppStep.FINAL_DRAFT, AppStep.GENERATING_DRAFT -> s.draft.isNotBlank()
        }
        if (canNavigate) {
            _state.value = s.copy(step = step, error = null)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun reset() {
        _state.value = UiState()
    }

    fun loadInputDraft(input: UserInput) {
        _state.value = _state.value.copy(userInput = input)
    }

    // ── Factory
    class Factory(
        private val orchestrator: AgentOrchestrator,
        private val planService: PlanService,
        private val draftService: DraftService,
        private val recommendationRepository: RecommendationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GenerationViewModel(orchestrator, planService, draftService, recommendationRepository) as T
    }
}
