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


    suspend fun upsert(profile: ProfessorProfile): Long = dao.insert(profile)


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
