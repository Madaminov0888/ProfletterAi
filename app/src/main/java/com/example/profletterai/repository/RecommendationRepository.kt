package com.example.profletterai.repository

import com.example.profletterai.data.local.RecommendationDao
import com.example.profletterai.data.model.LetterPlan
import com.example.profletterai.data.model.Profiles
import com.example.profletterai.data.model.SavedRecommendation
import com.example.profletterai.data.model.UserInput
import kotlinx.coroutines.flow.Flow

class RecommendationRepository(
    private val dao: RecommendationDao
) {
    fun observeAll(): Flow<List<SavedRecommendation>> = dao.observeAll()

    suspend fun getById(id: Long): SavedRecommendation? = dao.getById(id)

    suspend fun save(
        input: UserInput,
        profiles: Profiles,
        plan: LetterPlan,
        draftMarkdown: String
    ): Long = dao.insert(
        SavedRecommendation(
            recommenderName = input.recommenderName,
            studentName = input.studentName,
            targetProgram = input.targetProgram,
            draftMarkdown = draftMarkdown,
            planStructure = plan.structure,
            planReasoning = plan.reasoning,
            recommenderProfile = profiles.recommenderProfile,
            studentProfile = profiles.studentProfile,
            targetProfile = profiles.targetProfile,
            overlapAnalysis = profiles.overlapAnalysis
        )
    )

    suspend fun delete(id: Long) = dao.deleteById(id)
}
