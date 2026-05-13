package com.example.profletterai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_recommendations")
data class SavedRecommendation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recommenderName: String,
    val studentName: String,
    val targetProgram: String,
    val draftMarkdown: String,
    val planStructure: String,
    val planReasoning: String,
    val recommenderProfile: String,
    val studentProfile: String,
    val targetProfile: String,
    val overlapAnalysis: String,
    val createdAt: Long = System.currentTimeMillis()
)
