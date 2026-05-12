package com.example.profletterai.service.agent

import com.example.profletterai.data.model.UserInput
import com.example.profletterai.data.remote.GeminiClient

class StudentResearchAgent(
    private val gemini: GeminiClient
) : ResearchAgent {
    override val id = "student"
    override val displayName = "Student Profile Agent"
    override val description = "Distils the student's strengths, evidence and trajectory."

    override suspend fun run(input: UserInput): String {
        val resumeBlock = if (input.studentResume.isBlank()) "(none provided)"
        else input.studentResume.take(6000)

        val prompt = """
            You are an admissions strategist.

            Read the student's pasted background carefully and produce a Markdown
            profile that highlights:
              • strongest skills with concrete evidence
              • achievements suitable for letter quotation
              • narrative arc / trajectory
              • potential gaps or risks the recommender should pre-empt

            Be specific — never invent grades, projects or affiliations that are not
            implied by the input.

            STUDENT NAME: ${input.studentName}
            TARGET PROGRAM: ${input.targetProgram}
            HOW THE RECOMMENDER KNOWS THEM: ${input.recommenderContext}

            STUDENT BACKGROUND:
            $resumeBlock

            Return ONLY Markdown.
        """.trimIndent()
        return gemini.generate(prompt, temperature = 0.5f)
    }
}
