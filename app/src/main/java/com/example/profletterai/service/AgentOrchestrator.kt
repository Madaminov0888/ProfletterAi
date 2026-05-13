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


class AgentOrchestrator(
    private val recommenderAgent: RecommenderResearchAgent,
    private val studentAgent: StudentResearchAgent,
    private val targetAgent: TargetProgramAgent,
    private val institutionAgent: InstitutionContextAgent,
    private val overlapAgent: OverlapAnalysisAgent
) {

    private val _statuses = MutableStateFlow(initialStatuses())
    val statuses: StateFlow<List<AgentStatus>> = _statuses


    suspend fun buildProfiles(input: UserInput): Profiles = coroutineScope {
        _statuses.value = initialStatuses()

        val cachedRecommender = input.cachedRecommenderProfile?.takeIf { it.isNotBlank() }


        if (cachedRecommender != null) {
            setState(recommenderAgent.id, AgentState.DONE)
        } else {
            setState(recommenderAgent.id, AgentState.RUNNING)
        }
        setState(studentAgent.id, AgentState.RUNNING)
        setState(targetAgent.id, AgentState.RUNNING)
        setState(institutionAgent.id, AgentState.RUNNING)
        setState(overlapAgent.id, AgentState.IDLE)

        val recommenderDeferred = if (cachedRecommender != null) {
            async { cachedRecommender }
        } else {
            async { runAgent(recommenderAgent, input) }
        }
        val studentDeferred = async { runAgent(studentAgent, input) }
        val targetDeferred = async { runAgent(targetAgent, input) }
        val institutionDeferred = async { runAgent(institutionAgent, input) }

        val recommender = recommenderDeferred.await()
        val student = studentDeferred.await()
        val target = targetDeferred.await()
        val institution = institutionDeferred.await()


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

    private suspend fun runAgent(agent: ResearchAgent, input: UserInput): String =
        runCatching { agent.run(input) }
            .onSuccess { setState(agent.id, AgentState.DONE) }
            .onFailure { setState(agent.id, AgentState.FAILED) }
            .getOrThrow()

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
