package com.example.profletterai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.data.model.SavedRecommendation

@Database(
    entities = [SavedRecommendation::class, ProfessorProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recommendationDao(): RecommendationDao
    abstract fun professorProfileDao(): ProfessorProfileDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "profletter.db"
                )
                    // v1 → v2 added `enrichedProfile` and `researchField` columns. For this
                    // university-scope project we destructively rebuild rather than ship a
                    // hand-written migration; users can re-enter profiles after upgrading.
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
