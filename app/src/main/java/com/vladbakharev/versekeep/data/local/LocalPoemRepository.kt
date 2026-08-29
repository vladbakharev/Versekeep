package com.vladbakharev.versekeep.data.local

import android.content.ContentValues
import com.vladbakharev.versekeep.domain.model.Poem
import com.vladbakharev.versekeep.domain.repository.PoemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocalPoemRepository
@Inject
constructor(
    private val database: PoemDatabaseHelper,
) : PoemRepository {
    private val mutablePoems = MutableStateFlow<List<Poem>>(emptyList())
    override val poems: StateFlow<List<Poem>> = mutablePoems

    init {
        refresh()
    }

    override fun save(poem: Poem): Long {
        val now = System.currentTimeMillis()
        val values =
            ContentValues().apply {
                put("title", poem.title.trim())
                put("author", poem.author.trim())
                if (poem.year == null) putNull("year") else put("year", poem.year)
                put("content", poem.content.trim())
                put("favorite", if (poem.isFavorite) 1 else 0)
                if (poem.favoritedAt == null) putNull("favorited_at")
                else put("favorited_at", poem.favoritedAt)
                put("created_at", if (poem.id == 0L) now else poem.createdAt)
                put("updated_at", now)
            }
        val id =
            if (poem.id == 0L) {
                database.writableDatabase.insert("poems", null, values)
            } else {
                database.writableDatabase.update(
                    "poems",
                    values,
                    "id = ?",
                    arrayOf(poem.id.toString())
                )
                poem.id
            }
        refresh()
        return id
    }

    override fun delete(id: Long) {
        database.writableDatabase.delete("poems", "id = ?", arrayOf(id.toString()))
        refresh()
    }

    override fun toggleFavorite(id: Long) {
        database.writableDatabase.execSQL(
            """
            UPDATE poems
            SET favorite = 1 - favorite,
                favorited_at = CASE WHEN favorite = 0 THEN ? ELSE NULL END,
                updated_at = ?
            WHERE id = ?
            """.trimIndent(),
            arrayOf(System.currentTimeMillis(), System.currentTimeMillis(), id),
        )
        refresh()
    }

    private fun refresh() {
        val result = mutableListOf<Poem>()
        database.readableDatabase
            .query(
                "poems",
                null,
                null,
                null,
                null,
                null,
                "created_at DESC",
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val yearIndex = cursor.getColumnIndexOrThrow("year")
                    result +=
                        Poem(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                            author = cursor.getString(cursor.getColumnIndexOrThrow("author")),
                            year = if (cursor.isNull(yearIndex)) null else cursor.getInt(yearIndex),
                            content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                            isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1,
                            favoritedAt =
                                cursor.getColumnIndexOrThrow("favorited_at").let { index ->
                                    if (cursor.isNull(index)) null else cursor.getLong(index)
                                },
                            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")),
                        )
                }
            }
        mutablePoems.value = result
    }
}
