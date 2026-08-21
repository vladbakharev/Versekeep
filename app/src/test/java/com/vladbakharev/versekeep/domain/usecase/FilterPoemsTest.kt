package com.vladbakharev.versekeep.domain.usecase

import com.vladbakharev.versekeep.domain.model.Poem
import com.vladbakharev.versekeep.domain.model.PoemFilter
import com.vladbakharev.versekeep.domain.model.PoemSort
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterPoemsTest {
    private val poems =
        listOf(
            Poem(id = 1, title = "The Road", author = "Frost", year = 1915, content = "roads", createdAt = 1),
            Poem(id = 2, title = "Hope", author = "Dickinson", year = 1861, content = "feathers", createdAt = 2),
        )

    @Test fun `searches all text fields ignoring case`() {
        assertEquals(listOf(2L), FilterPoems()(poems, PoemFilter(query = "FEATHERS")).map(Poem::id))
    }

    @Test fun `filters and sorts without presentation dependencies`() {
        val result = FilterPoems()(poems, PoemFilter(author = "frost", sort = PoemSort.TITLE))
        assertEquals(listOf(1L), result.map(Poem::id))
    }
}
