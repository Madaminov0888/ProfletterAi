package com.example.profletterai.data.model

/**
 * State of a single research agent — used to render the live "5 agents thinking" loader.
 */
data class AgentStatus(
    val id: String,
    val name: String,
    val description: String,
    val state: AgentState = AgentState.IDLE
)

enum class AgentState { IDLE, RUNNING, DONE, FAILED }
