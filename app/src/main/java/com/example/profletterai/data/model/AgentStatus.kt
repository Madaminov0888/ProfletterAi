package com.example.profletterai.data.model


data class AgentStatus(
    val id: String,
    val name: String,
    val description: String,
    val state: AgentState = AgentState.IDLE
)

enum class AgentState { IDLE, RUNNING, DONE, FAILED }
