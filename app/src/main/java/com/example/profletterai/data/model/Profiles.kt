package com.example.profletterai.data.model

/**
 * Combined intelligence profiles produced by the parallel research agents.
 * Markdown strings — rendered with the lightweight markdown component.
 */
data class Profiles(
    val recommenderProfile: String,
    val studentProfile: String,
    val targetProfile: String,
    val overlapAnalysis: String,
    val institutionalContext: String = ""
)
