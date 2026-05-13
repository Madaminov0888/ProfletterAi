package com.example.profletterai.service.agent

import com.example.profletterai.data.model.UserInput
import com.example.profletterai.data.remote.GeminiClient

class RecommenderResearchAgent(
    private val gemini: GeminiClient
) : ResearchAgent {
    override val id = "recommender"
    override val displayName = "Recommender Profile Agent"
    override val description = "Researches the professor's voice, research areas and credibility."

    override suspend fun run(input: UserInput): String {
        val prompt = """
            You are an academic intelligence analyst.

            Build a concise but rich Markdown profile of the RECOMMENDER below.
            Focus on what would make their letter persuasive:
              • field / research interests
              • likely vocabulary, tone, formality
              • institution prestige and culture
              • authority signals you can reasonably infer

            If you do not know a verifiable fact, state assumptions explicitly
            instead of inventing details.

            RECOMMENDER:
              Name: ${input.recommenderName}
              Institution: ${input.recommenderInstitution}
              How they know the student: ${input.recommenderContext}

            Return ONLY Markdown — no JSON, no preamble.
        """.trimIndent()
        return gemini.generate(prompt, temperature = 0.5f)
    }
}
