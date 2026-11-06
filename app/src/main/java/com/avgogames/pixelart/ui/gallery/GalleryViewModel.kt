package com.avgogames.pixelart.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.avgogames.pixelart.core.data.ArtRepository
import com.avgogames.pixelart.core.model.ArtSummary
import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.ui.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GalleryUiState(
    val isLoading: Boolean = true,
    val selectedCategory: Category = Category.ANIME,
    val items: List<ArtSummary> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
)

class GalleryViewModel(
    private val savedState: SavedStateHandle,
    private val repository: ArtRepository,
) : ViewModel() {

    private val selectedCategory = savedState.getStateFlow(KEY_CATEGORY, Category.ANIME)

    val uiState: StateFlow<GalleryUiState> =
        combine(repository.summaries, selectedCategory) { summaries, category ->
            GalleryUiState(
                isLoading = false,
                selectedCategory = category,
                items = summaries.filter { it.art.category == category },
                completedCount = summaries.count { it.progress.isComplete },
                totalCount = summaries.size,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GalleryUiState())

    fun selectCategory(category: Category) {
        savedState[KEY_CATEGORY] = category
    }

    fun deleteImported(id: String) {
        viewModelScope.launch { repository.deleteImported(id) }
    }

    companion object {
        private const val KEY_CATEGORY = "category"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { GalleryViewModel(createSavedStateHandle(), appContainer().repository) }
        }
    }
}
