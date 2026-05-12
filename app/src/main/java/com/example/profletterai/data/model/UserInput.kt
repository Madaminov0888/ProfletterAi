package com.example.profletterai.data.model

/**
 * Mirrors the [UserInput] interface from the web project's `types.ts`.
 * The Android version drops file attachments to keep the university scope
 * focused — student background is supplied as pasted text and target links.
 */
data class UserInput(
    val recommenderName: String,
    val recommenderInstitution: String,
    val recommenderContext: String,
    val studentName: String,
    val studentResume: String,
    val targetProgram: String,
    val targetLinks: List<String> = emptyList()
)
