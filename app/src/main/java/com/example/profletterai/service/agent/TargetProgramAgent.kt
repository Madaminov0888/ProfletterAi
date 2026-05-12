package com.example.profletterai.service.agent

import com.example.profletterai.data.model.UserInput
import com.example.profletterai.data.remote.GeminiClient

class TargetProgramAgent(
    private val gemini: GeminiClient
) : ResearchAgent {
    override val id = "target"
    override val displayName = "Target Program Agent"
    override val description = "Maps the program's mission, values and admission signals."

    override suspend fun run(input: UserInput): String {
        val linksBlock = if (input.targetLinks.isEmpty()) "(none)"
        else input.targetLinks.joinToString("\n") { "- $it" }

        val prompt = """
            You are an academic researcher who specialises in graduate program profiling.

            For the TARGET PROGRAM below, produce a Markdown profile covering:
              • program mission and stated values
              • the kind of applicant they typically admit
              • known faculty / labs that are noteworthy in this domain
              • current strategic priorities (research areas, initiatives)

            Reference the supplied URLs when possible. If you have no reliable
            knowledge, say so honestly rather than inventing facts.

            TARGET PROGRAM: ${input.targetProgram}
            REFERENCE LINKS:
            $linksBlock

            Return ONLY Markdown.
        """.trimIndent()
        return gemini.generate(prompt, temperature = 0.4f)
    }
}
