package com.example.profletterai.service

import com.example.profletterai.data.model.LetterPlan
import com.example.profletterai.data.model.Profiles
import com.example.profletterai.data.model.UserInput
import com.example.profletterai.data.remote.GeminiClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class PlanService(
    private val gemini: GeminiClient
) {

    @Serializable
    private data class PlanDto(val reasoning: String = "", val structure: String = "")

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun generatePlan(
        input: UserInput,
        profiles: Profiles,
        previousPlan: LetterPlan? = null,
        feedback: String? = null
    ): LetterPlan {
        val regenBlock = if (previousPlan != null && !feedback.isNullOrBlank()) """
            PREVIOUS PLAN STRUCTURE:
            ${previousPlan.structure}

            PREVIOUS REASONING:
            ${previousPlan.reasoning}

            USER FEEDBACK:
            "$feedback"

            Adjust the new plan according to that feedback.
        """.trimIndent() else ""

        val prompt = """
            You are an expert academic writer acting as ${input.recommenderName}.

            You will draft a strong letter of recommendation for ${input.studentName}
            applying to ${input.targetProgram}.

            Use the dossiers below as your source of truth.

            === RECOMMENDER PROFILE ===
            ${profiles.recommenderProfile}

            === STUDENT PROFILE ===
            ${profiles.studentProfile}

            === TARGET PROGRAM PROFILE ===
            ${profiles.targetProfile}

            === STRATEGIC OVERLAP ===
            ${profiles.overlapAnalysis}

            $regenBlock

            TASK
            Produce a JSON object with two fields:
              - "reasoning": a short paragraph explaining the strategy
              - "structure": a Markdown outline with paragraph-by-paragraph
                bullet points of evidence. Don't write the full letter yet.

            STRICT JSON: no comments, no trailing commas, escape newlines properly.
        """.trimIndent()

        val raw = gemini.generate(
            prompt = prompt,
            temperature = 0.5f,
            responseAsJson = true
        )

        val cleaned = stripFences(raw)
        val dto = runCatching { json.decodeFromString(PlanDto.serializer(), cleaned) }
            .getOrElse {

                PlanDto(
                    reasoning = "Model returned non-JSON; using best effort.",
                    structure = cleaned
                )
            }

        return LetterPlan(structure = dto.structure, reasoning = dto.reasoning)
    }

    private fun stripFences(s: String): String {
        val trimmed = s.trim()
        val withoutFence = trimmed
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```")
            .trim()

        val start = withoutFence.indexOf('{')
        val end = withoutFence.lastIndexOf('}')
        return if (start >= 0 && end > start) withoutFence.substring(start, end + 1) else withoutFence
    }
}
