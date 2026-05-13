package com.example.profletterai.data.model


data class Profiles(
    val recommenderProfile: String,
    val studentProfile: String,
    val targetProfile: String,
    val overlapAnalysis: String,
    val institutionalContext: String = ""
)
