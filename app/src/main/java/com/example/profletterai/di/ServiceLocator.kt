package com.example.profletterai.di

import android.content.Context
import com.example.profletterai.data.local.AppDatabase
import com.example.profletterai.data.remote.GeminiClient
import com.example.profletterai.repository.ProfessorProfileRepository
import com.example.profletterai.repository.RecommendationRepository
import com.example.profletterai.service.AgentOrchestrator
import com.example.profletterai.service.DraftService
import com.example.profletterai.service.PlanService
import com.example.profletterai.service.agent.InstitutionContextAgent
import com.example.profletterai.service.agent.OverlapAnalysisAgent
import com.example.profletterai.service.agent.ProfileEnrichmentAgent
import com.example.profletterai.service.agent.RecommenderResearchAgent
import com.example.profletterai.service.agent.StudentResearchAgent
import com.example.profletterai.service.agent.TargetProgramAgent

/**
 * Tiny manual DI container.
 *
 * Intentionally NOT using Hilt — the project is small enough that a single
 * lazy object keeps onboarding simple for a university project. Everything
 * is constructed once at app start.
 */
class ServiceLocator(appContext: Context) {

    // ── Networking
    val geminiClient = GeminiClient()

    // ── Agents
    private val recommenderAgent = RecommenderResearchAgent(geminiClient)
    private val studentAgent = StudentResearchAgent(geminiClient)
    private val targetAgent = TargetProgramAgent(geminiClient)
    private val institutionAgent = InstitutionContextAgent(geminiClient)
    private val overlapAgent = OverlapAnalysisAgent(geminiClient)
    private val profileEnrichmentAgent = ProfileEnrichmentAgent(geminiClient)

    // ── Database / repos
    private val database = AppDatabase.get(appContext)
    val recommendationRepository = RecommendationRepository(database.recommendationDao())
    val professorProfileRepository = ProfessorProfileRepository(
        dao = database.professorProfileDao(),
        enrichmentAgent = profileEnrichmentAgent
    )

    // ── Services
    val agentOrchestrator = AgentOrchestrator(
        recommenderAgent = recommenderAgent,
        studentAgent = studentAgent,
        targetAgent = targetAgent,
        institutionAgent = institutionAgent,
        overlapAgent = overlapAgent
    )
    val planService = PlanService(geminiClient)
    val draftService = DraftService(geminiClient)
}
