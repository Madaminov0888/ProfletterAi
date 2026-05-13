package com.example.profletterai.service.agent

import com.example.profletterai.data.model.UserInput
import com.example.profletterai.data.remote.GeminiClient


class InstitutionContextAgent(
    private val gemini: GeminiClient
) : ResearchAgent {
    override val id = "institution"
    override val displayName = "Institutional Context Agent"
    override val description = "Surfaces field-specific norms and current admissions trends."

    override suspend fun run(input: UserInput): String {
        val prompt = """
            You are an industry analyst writing briefing notes for a letter writer.

            For the field implied by ${input.targetProgram} (and adjacent areas),
            produce a SHORT Markdown briefing on:
              • what selection committees in this field currently weight most
              • common pitfalls in recommendation letters for this kind of program
              • language and framing that resonates well in this domain
              • one or two recent themes the letter could allude to

            Be concrete and field-specific. Do not pad. Three to six bullet points
            per section is plenty.

            Return ONLY Markdown.
        """.trimIndent()
        return gemini.generate(prompt, temperature = 0.4f)
    }
}
