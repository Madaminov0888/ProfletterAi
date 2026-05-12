package com.example.profletterai.service

import com.example.profletterai.data.model.LetterPlan
import com.example.profletterai.data.model.Profiles
import com.example.profletterai.data.model.UserInput
import com.example.profletterai.data.remote.GeminiClient

/**
 * Final-step service: takes the approved plan + profiles and writes the
 * full Markdown recommendation letter.
 */
class DraftService(
    private val gemini: GeminiClient
) {
    suspend fun generateDraft(
        input: UserInput,
        profiles: Profiles,
        plan: LetterPlan
    ): String {
        val prompt = """
            Act as Professor ${input.recommenderName} from ${input.recommenderInstitution}.

            Write the full recommendation letter for ${input.studentName} applying to
            ${input.targetProgram}, following the approved plan strictly.

            === APPROVED PLAN ===
            ${plan.structure}

            === RECOMMENDER VOICE ===
            ${profiles.recommenderProfile}

            === STUDENT EVIDENCE ===
            ${profiles.studentProfile}

            === AUDIENCE ===
            ${profiles.targetProfile}

            === STRATEGIC ANGLE ===
            ${profiles.overlapAnalysis}

            STYLE
              - Formal academic letter format (date, salutation, body, signature)
              - Confident yet evidence-based; no hyperbole
              - Match vocabulary cues from the recommender profile
              - 4–6 paragraphs

            OUTPUT
            Return the complete letter as Markdown only — no preamble, no JSON.
        """.trimIndent()

        return gemini.generate(prompt, temperature = 0.7f)
    }

    /**
     * Lightweight selection-rewrite used by the draft editor.
     */
    suspend fun rewriteSelection(
        selection: String,
        instruction: String,
        fullDraftContext: String
    ): String {
        val prompt = """
            You are an expert editor. Rewrite the selected snippet from a
            recommendation letter according to the user's instruction.
            Match tone of the surrounding context.

            CONTEXT (excerpt):
            ${fullDraftContext.take(1200)}

            SELECTED TEXT:
            "$selection"

            INSTRUCTION:
            "$instruction"

            Return ONLY the rewritten text — no quotes, no explanations.
        """.trimIndent()
        return gemini.generate(prompt, temperature = 0.6f)
    }
}
