package com.example.profletterai.service.agent

import com.example.profletterai.data.model.UserInput

/**
 * A single specialised research agent. Each implementation owns ONE prompt
 * and produces a markdown chunk that the orchestrator stitches together.
 *
 * Keeping each agent in its own class — instead of stuffing branching logic
 * into the ViewModel — is the architectural promise the user asked for:
 * services do work, ViewModels coordinate.
 */
interface ResearchAgent {
    /** Stable id, also used for the live status UI. */
    val id: String

    /** Human-readable name shown in the agent loader. */
    val displayName: String

    /** Short subtitle shown in the agent loader. */
    val description: String

    /** Run the agent and return its markdown finding. */
    suspend fun run(input: UserInput): String
}
