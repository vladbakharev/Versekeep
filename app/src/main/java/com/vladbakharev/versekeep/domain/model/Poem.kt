package com.vladbakharev.versekeep.domain.model

data class Poem(
    val id: Long = 0,
    val title: String,
    val author: String,
    val year: Int?,
    val content: String,
    val isFavorite: Boolean = false,
    val favoritedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

enum class PoemSort { RECENT, TITLE, AUTHOR, YEAR }

data class PoemFilter(
    val query: String = "",
    val author: String = "",
    val year: Int? = null,
    val sort: PoemSort = PoemSort.RECENT,
)
