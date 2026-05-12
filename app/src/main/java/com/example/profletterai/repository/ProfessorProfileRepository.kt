package com.example.profletterai.repository

import com.example.profletterai.data.local.ProfessorProfileDao
import com.example.profletterai.data.model.ProfessorProfile
import kotlinx.coroutines.flow.Flow

class ProfessorProfileRepository(
    private val dao: ProfessorProfileDao
) {
    fun observeAll(): Flow<List<ProfessorProfile>> = dao.observeAll()

    suspend fun upsert(profile: ProfessorProfile): Long = dao.insert(profile)

    suspend fun delete(profile: ProfessorProfile) = dao.delete(profile)
}
