package com.example.profletterai.service.agent

import com.example.profletterai.data.remote.GeminiClient


class OverlapAnalysisAgent(
    private val gemini: GeminiClient
) {
    val id = "overlap"
    val displayName = "Overlap Analysis Agent"
    val description = "Cross-references all profiles to find the strategic angle."

    suspend fun run(
        recommender: String,
        student: String,
        target: String,
        institution: String
    ): String {
        val prompt = """
            You are the lead strategist on a writing team.

            Below are four research dossiers. Your job is to write the STRATEGIC
            OVERLAP ANALYSIS in Markdown — i.e. the specific angle the
            recommendation letter should take to make this student a compelling fit.

            Output structure:
              ## Strategic angle
              (2–3 sentences naming the single sharpest narrative)

              ## Strongest evidence to feature
              - bullet 1
              - bullet 2
              ...

              ## What to downplay or reframe
              - …

              ## Field-specific cues the letter should hit
              - …

            ============== RECOMMENDER PROFILE ==============
            $recommender

            ============== STUDENT PROFILE ==============
            $student

            ============== TARGET PROGRAM PROFILE ==============
            $target

            ============== INSTITUTIONAL CONTEXT ==============
            $institution

            Return ONLY Markdown.
        """.trimIndent()
        return gemini.generate(prompt, temperature = 0.5f)
    }
}
