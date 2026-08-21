package com.vladbakharev.versekeep.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladbakharev.versekeep.domain.model.Poem
import com.vladbakharev.versekeep.domain.model.PoemFilter
import com.vladbakharev.versekeep.domain.usecase.PoemUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VersekeepViewModel
    @Inject
    constructor(
        private val useCases: PoemUseCases,
    ) : ViewModel() {
        val poems: StateFlow<List<Poem>> = useCases.observePoems()
        val filter = MutableStateFlow(PoemFilter())
        val filteredPoems =
            combine(poems, filter, useCases.filterPoems::invoke)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        fun updateFilter(transform: (PoemFilter) -> PoemFilter) {
            filter.value = transform(filter.value)
        }

        fun clearFilter() {
            filter.value = PoemFilter()
        }

        fun find(id: Long): Poem? = useCases.getPoem(id)

        fun save(poem: Poem): Long = useCases.savePoem(poem)

        fun delete(id: Long) = useCases.deletePoem(id)

        fun toggleFavorite(id: Long) = useCases.toggleFavorite(id)
    }
