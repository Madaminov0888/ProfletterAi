package com.example.profletterai.repository

import com.example.profletterai.data.local.ProfessorProfileDao
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.service.agent.ProfileEnrichmentAgent
import kotlinx.coroutines.flow.Flow

class ProfessorProfileRepository(
    private val dao: ProfessorProfileDao,
    private val enrichmentAgent: ProfileEnrichmentAgent
) {
    fun observeAll(): Flow<List<ProfessorProfile>> = dao.observeAll()

    /**
     * Persists a profile without running enrichment — used for fast UI
     * scenarios like renames where we don't want a network roundtrip.
     */
    suspend fun upsert(profile: ProfessorProfile): Long = dao.insert(profile)

    /**
     * Persists a profile and, if the input fields changed in a way that
     * invalidates the cached enrichment, kicks off the enrichment agent
     * before saving. On enrichment failure we still save the profile —
     * the user shouldn't be blocked by a flaky network.
     */
    suspend fun upsertWithEnrichment(profile: ProfessorProfile): Long {
        val needsEnrichment = profile.enrichedProfile.isBlank() ||
            profile.recommenderName.isNotBlank() && profile.recommenderInstitution.isNotBlank()
        val enriched = if (needsEnrichment) {
            runCatching { enrichmentAgent.enrich(profile) }.getOrNull()
        } else null

        val toSave = if (enriched != null) {
            profile.copy(
                enrichedProfile = enriched.enrichedProfile,
                researchField = enriched.researchField
            )
        } else profile

        return dao.insert(toSave)
    }

    suspend fun delete(profile: ProfessorProfile) = dao.delete(profile)
}
