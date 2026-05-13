package com.example.profletterai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "professor_profiles")
data class ProfessorProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val recommenderName: String,
    val recommenderInstitution: String,
    val defaultContext: String = "",
    val notes: String = "",

    val enrichedProfile: String = "",

    val researchField: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
