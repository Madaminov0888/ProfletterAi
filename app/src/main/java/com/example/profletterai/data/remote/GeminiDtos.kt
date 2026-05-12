package com.example.profletterai.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Minimal request/response DTOs for the Gemini REST API.
 *
 * Endpoint:
 *  POST https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key=API_KEY
 */

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null,
    val safetySettings: List<GeminiSafetySetting>? = null
)

@Serializable
data class GeminiContent(
    val role: String? = null,                // "user" | "model"
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null,
    val responseMimeType: String? = null,
    val candidateCount: Int? = null
)

@Serializable
data class GeminiSafetySetting(
    val category: String,
    val threshold: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val promptFeedback: GeminiPromptFeedback? = null,
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null,
    val index: Int? = null
)

@Serializable
data class GeminiPromptFeedback(
    val blockReason: String? = null
)

@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

/**
 * Pulls the first text part out of the first candidate.
 */
fun GeminiResponse.firstText(): String? =
    candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
