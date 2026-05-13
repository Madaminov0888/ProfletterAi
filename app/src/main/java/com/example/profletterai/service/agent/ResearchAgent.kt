package com.example.profletterai.service.agent

import com.example.profletterai.data.model.UserInput


interface ResearchAgent {

    val id: String


    val displayName: String


    val description: String


    suspend fun run(input: UserInput): String
}
