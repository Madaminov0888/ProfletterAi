package com.example.profletterai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Saved professor (recommender) profile — lets the user store reusable
 * personas and quickly load them into the Generate form.
 *
 * The first time a profile is saved we run a one-shot enrichment agent
 * over the entered fields and stash the markdown result in [enrichedProfile].
 * When this profile is later picked on the Generate tab, the Recommender
 * Profile Agent can skip its network call entirely and reuse the cached
 * markdown — that's why the "Recommender Profile Agent" step often
 * completes instantly when a saved profile is used.
 */
@Entity(tableName = "professor_profiles")
data class ProfessorProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,            // e.g. "Dr. Jane Smith — Stanford CS"
    val recommenderName: String,
    val recommenderInstitution: String,
    val defaultContext: String = "",    // boilerplate "how I know the student" prefix
    val notes: String = "",
    /** Cached markdown produced by [com.example.profletterai.service.agent.ProfileEnrichmentAgent]. */
    val enrichedProfile: String = "",
    /** Short, comma-separated research field summary (e.g. "Machine Learning, NLP"). */
    val researchField: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
