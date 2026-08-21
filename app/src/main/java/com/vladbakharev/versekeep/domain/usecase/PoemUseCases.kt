package com.vladbakharev.versekeep.domain.usecase

import com.vladbakharev.versekeep.domain.model.Poem
import com.vladbakharev.versekeep.domain.model.PoemFilter
import com.vladbakharev.versekeep.domain.model.PoemSort
import com.vladbakharev.versekeep.domain.repository.PoemRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObservePoems
    @Inject
    constructor(
        private val repository: PoemRepository,
    ) {
        operator fun invoke(): StateFlow<List<Poem>> = repository.poems
    }

class GetPoem
    @Inject
    constructor(
        private val repository: PoemRepository,
    ) {
        operator fun invoke(id: Long): Poem? = repository.poems.value.firstOrNull { it.id == id }
    }

class SavePoem
    @Inject
    constructor(
        private val repository: PoemRepository,
    ) {
        operator fun invoke(poem: Poem): Long = repository.save(poem)
    }

class DeletePoem
    @Inject
    constructor(
        private val repository: PoemRepository,
    ) {
        operator fun invoke(id: Long) = repository.delete(id)
    }

class ToggleFavorite
    @Inject
    constructor(
        private val repository: PoemRepository,
    ) {
        operator fun invoke(id: Long) = repository.toggleFavorite(id)
    }

class FilterPoems
    @Inject
    constructor() {
        operator fun invoke(
            poems: List<Poem>,
            filter: PoemFilter,
        ): List<Poem> =
            poems
                .filter { poem ->
                    val queryMatch =
                        filter.query.isBlank() ||
                            listOf(poem.title, poem.author, poem.content).any {
                                it.contains(filter.query, ignoreCase = true)
                            }
                    val authorMatch =
                        filter.author.isBlank() ||
                            poem.author.equals(filter.author, ignoreCase = true)
                    val yearMatch = filter.year == null || poem.year == filter.year
                    queryMatch && authorMatch && yearMatch
                }.let { result ->
                    when (filter.sort) {
                        PoemSort.RECENT -> result.sortedByDescending(Poem::createdAt)
                        PoemSort.TITLE -> result.sortedBy { it.title.lowercase() }
                        PoemSort.AUTHOR -> result.sortedBy { it.author.lowercase() }
                        PoemSort.YEAR -> result.sortedByDescending { it.year ?: Int.MIN_VALUE }
                    }
                }
    }

data class PoemUseCases
    @Inject
    constructor(
        val observePoems: ObservePoems,
        val getPoem: GetPoem,
        val filterPoems: FilterPoems,
        val savePoem: SavePoem,
        val deletePoem: DeletePoem,
        val toggleFavorite: ToggleFavorite,
    )
