package com.example.profletterai.data.remote

import com.example.profletterai.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit


class GeminiClient(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val baseUrl: String = "https://generativelanguage.googleapis.com/"
) {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = false
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        })
        .build()

    private val api: GeminiApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(GeminiApi::class.java)


    suspend fun generate(
        prompt: String,
        systemInstruction: String? = null,
        model: String = DEFAULT_MODEL,
        temperature: Float = 0.7f,
        responseAsJson: Boolean = false,
        maxOutputTokens: Int? = null
    ): String {
        require(apiKey.isNotBlank() && apiKey != "PASTE_YOUR_KEY") {
            "Missing Gemini API key. Add `gemini.api.key=YOUR_KEY` to local.properties " +
                "(get a free key from https://aistudio.google.com/apikey) and rebuild."
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(role = "user", parts = listOf(GeminiPart(prompt)))
            ),
            systemInstruction = systemInstruction?.let {
                GeminiContent(parts = listOf(GeminiPart(it)))
            },
            generationConfig = GeminiGenerationConfig(
                temperature = temperature,
                responseMimeType = if (responseAsJson) "application/json" else null,
                maxOutputTokens = maxOutputTokens
            )
        )

        val response = api.generateContent(model = model, apiKey = apiKey, request = request)

        response.error?.let { err ->
            throw IllegalStateException("Gemini API error ${err.code ?: ""}: ${err.message ?: "unknown"}")
        }

        val text = response.firstText()
            ?: throw IllegalStateException(
                "Gemini returned no text" +
                    (response.candidates?.firstOrNull()?.finishReason?.let { " (finishReason=$it)" } ?: "") +
                    (response.promptFeedback?.blockReason?.let { " (blockReason=$it)" } ?: "")
            )

        return text
    }

    companion object {


        const val DEFAULT_MODEL = "gemini-flash-latest"
    }
}
