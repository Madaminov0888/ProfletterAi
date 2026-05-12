package com.example.profletterai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.profletterai.data.model.SavedRecommendation
import com.example.profletterai.repository.RecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedRecommendationsViewModel(
    private val repository: RecommendationRepository
) : ViewModel() {

    val items: StateFlow<List<SavedRecommendation>> =
        repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selected = MutableStateFlow<SavedRecommendation?>(null)
    val selected: StateFlow<SavedRecommendation?> = _selected.asStateFlow()

    fun select(item: SavedRecommendation) { _selected.value = item }
    fun clearSelection() { _selected.value = null }

    fun delete(item: SavedRecommendation) {
        viewModelScope.launch { repository.delete(item.id) }
        if (_selected.value?.id == item.id) _selected.value = null
    }

    class Factory(private val repository: RecommendationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SavedRecommendationsViewModel(repository) as T
    }
}
