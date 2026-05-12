package com.example.profletterai.service

import com.example.profletterai.data.model.AgentState
import com.example.profletterai.data.model.AgentStatus
import com.example.profletterai.data.model.Profiles
import com.example.profletterai.data.model.UserInput
import com.example.profletterai.service.agent.InstitutionContextAgent
import com.example.profletterai.service.agent.OverlapAnalysisAgent
import com.example.profletterai.service.agent.RecommenderResearchAgent
import com.example.profletterai.service.agent.ResearchAgent
import com.example.profletterai.service.agent.StudentResearchAgent
import com.example.profletterai.service.agent.TargetProgramAgent
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Coordinates the 5 parallel research agents + 1 synthesizer.
 *
 * Exposes a [statuses] StateFlow so the loader UI can show each agent
 * flipping from RUNNING → DONE / FAILED in real time. The ViewModel
 * just observes; it never has to know about coroutines or coordination.
 */
class AgentOrchestrator(
    private val recommenderAgent: RecommenderResearchAgent,
    private val studentAgent: StudentResearchAgent,
    private val targetAgent: TargetProgramAgent,
    private val institutionAgent: InstitutionContextAgent,
    private val overlapAgent: OverlapAnalysisAgent
) {

    private val parallelAgents: List<ResearchAgent> = listOf(
        recommenderAgent, studentAgent, targetAgent, institutionAgent
    )

    private val _statuses = MutableStateFlow(initialStatuses())
    val statuses: StateFlow<List<AgentStatus>> = _statuses

    /**
     * Kicks off all parallel agents at once, then runs the overlap synthesizer
     * once they all return. Status flow is updated as each agent transitions.
     */
    suspend fun buildProfiles(input: UserInput): Profiles = coroutineScope {
        _statuses.value = initialStatuses()

        // Mark every agent RUNNING up-front so the loader animates simultaneously.
        parallelAgents.forEach { setState(it.id, AgentState.RUNNING) }
        setState(overlapAgent.id, AgentState.IDLE)

        val deferreds = parallelAgents.map { agent ->
            async {
                runCatching { agent.run(input) }
                    .onSuccess { setState(agent.id, AgentState.DONE) }
                    .onFailure { setState(agent.id, AgentState.FAILED) }
                    .getOrThrow()
            }
        }

        val recommender = deferreds[0].await()
        val student     = deferreds[1].await()
        val target      = deferreds[2].await()
        val institution = deferreds[3].await()

        // 5th synthesizer: cross-reference everything into the overlap.
        setState(overlapAgent.id, AgentState.RUNNING)
        val overlap = runCatching { overlapAgent.run(recommender, student, target, institution) }
            .onSuccess { setState(overlapAgent.id, AgentState.DONE) }
            .onFailure { setState(overlapAgent.id, AgentState.FAILED) }
            .getOrThrow()

        Profiles(
            recommenderProfile = recommender,
            studentProfile = student,
            targetProfile = target,
            overlapAnalysis = overlap,
            institutionalContext = institution
        )
    }

    private fun setState(id: String, state: AgentState) {
        _statuses.update { list ->
            list.map { if (it.id == id) it.copy(state = state) else it }
        }
    }

    private fun initialStatuses(): List<AgentStatus> = listOf(
        AgentStatus(recommenderAgent.id, recommenderAgent.displayName, recommenderAgent.description),
        AgentStatus(studentAgent.id, studentAgent.displayName, studentAgent.description),
        AgentStatus(targetAgent.id, targetAgent.displayName, targetAgent.description),
        AgentStatus(institutionAgent.id, institutionAgent.displayName, institutionAgent.description),
        AgentStatus(overlapAgent.id, overlapAgent.displayName, overlapAgent.description)
    )
}
