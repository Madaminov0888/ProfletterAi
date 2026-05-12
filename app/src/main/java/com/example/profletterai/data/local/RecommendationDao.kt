package com.example.profletterai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.profletterai.data.model.SavedRecommendation
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM saved_recommendations ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SavedRecommendation>>

    @Query("SELECT * FROM saved_recommendations WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SavedRecommendation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recommendation: SavedRecommendation): Long

    @Delete
    suspend fun delete(recommendation: SavedRecommendation)

    @Query("DELETE FROM saved_recommendations WHERE id = :id")
    suspend fun deleteById(id: Long)
}
