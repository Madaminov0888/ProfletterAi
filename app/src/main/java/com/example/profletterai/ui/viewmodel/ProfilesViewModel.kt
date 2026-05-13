package com.example.profletterai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.profletterai.data.model.ProfessorProfile
import com.example.profletterai.repository.ProfessorProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val repository: ProfessorProfileRepository
) : ViewModel() {

    val profiles: StateFlow<List<ProfessorProfile>> =
        repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    private val _isEnriching = MutableStateFlow(false)
    val isEnriching: StateFlow<Boolean> = _isEnriching.asStateFlow()

    private val _enrichmentError = MutableStateFlow<String?>(null)
    val enrichmentError: StateFlow<String?> = _enrichmentError.asStateFlow()


    fun upsert(profile: ProfessorProfile) {
        viewModelScope.launch {
            _isEnriching.value = true
            _enrichmentError.value = null
            runCatching { repository.upsertWithEnrichment(profile) }
                .onFailure { e -> _enrichmentError.value = e.message ?: "Failed to enrich profile." }
            _isEnriching.value = false
        }
    }

    fun delete(profile: ProfessorProfile) {
        viewModelScope.launch { repository.delete(profile) }
    }

    fun clearError() { _enrichmentError.value = null }

    class Factory(private val repository: ProfessorProfileRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfilesViewModel(repository) as T
    }
}
