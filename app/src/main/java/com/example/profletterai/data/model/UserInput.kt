package com.example.profletterai.data.model


data class UserInput(
    val recommenderName: String,
    val recommenderInstitution: String,
    val recommenderContext: String,
    val studentName: String,
    val studentResume: String,
    val targetProgram: String,
    val targetLinks: List<String> = emptyList(),
    val cachedRecommenderProfile: String? = null
)
