package com.vladbakharev.versekeep.domain.repository

import com.vladbakharev.versekeep.domain.model.Poem
import kotlinx.coroutines.flow.StateFlow

interface PoemRepository {
    val poems: StateFlow<List<Poem>>

    fun save(poem: Poem): Long

    fun delete(id: Long)

    fun toggleFavorite(id: Long)
}
