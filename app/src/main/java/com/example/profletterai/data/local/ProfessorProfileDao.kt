package com.example.profletterai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.profletterai.data.model.ProfessorProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfessorProfileDao {

    @Query("SELECT * FROM professor_profiles ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ProfessorProfile>>

    @Query("SELECT * FROM professor_profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProfessorProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfessorProfile): Long

    @Update
    suspend fun update(profile: ProfessorProfile)

    @Delete
    suspend fun delete(profile: ProfessorProfile)
}
