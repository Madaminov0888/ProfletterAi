package com.example.profletterai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Saved professor (recommender) profile — lets the user store reusable
 * personas and quickly load them into the Generate form.
 */
@Entity(tableName = "professor_profiles")
data class ProfessorProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,            // e.g. "Dr. Jane Smith — Stanford CS"
    val recommenderName: String,
    val recommenderInstitution: String,
    val defaultContext: String = "",    // boilerplate "how I know the student" prefix
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
