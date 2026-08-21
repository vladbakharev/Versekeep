package com.vladbakharev.versekeep.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vladbakharev.versekeep.domain.model.Poem
import com.vladbakharev.versekeep.domain.model.PoemFilter
import com.vladbakharev.versekeep.domain.model.PoemSort

@Composable
fun HomeScreen(
    poems: List<Poem>,
    onPoem: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 16.dp)) {
            Text("Versekeep", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("A quiet place for the poems you love", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (poems.isEmpty()) {
            EmptyState(
                Icons.Default.AutoStories,
                "Your collection starts here",
                "Save a poem and return to its words whenever you like.",
                "Add your first poem",
                onAdd,
            )
        } else {
            Text(
                "Recently added",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
            PoemList(poems, onPoem)
        }
    }
}

@Composable
fun LibraryScreen(
    poems: List<Poem>,
    allPoems: List<Poem>,
    filter: PoemFilter,
    onFilter: ((PoemFilter) -> PoemFilter) -> Unit,
    onClear: () -> Unit,
    onPoem: (Long) -> Unit,
) {
    var filtersOpen by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Text(
            "Library",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp),
        )
        OutlinedTextField(
            value = filter.query,
            onValueChange = { query -> onFilter { it.copy(query = query) } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            placeholder = { Text("Search poems, authors, or words") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = { IconButton(onClick = { filtersOpen = true }) { Icon(Icons.Default.Tune, "Filters") } },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )
        val active = filter.author.isNotBlank() || filter.year != null || filter.sort != PoemSort.RECENT
        if (active) {
            Row(
                Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (filter.author.isNotBlank()) {
                    InputChip(
                        true,
                        { onFilter { it.copy(author = "") } },
                        { Text(filter.author) },
                    )
                }
                if (filter.year != null) {
                    InputChip(
                        true,
                        { onFilter { it.copy(year = null) } },
                        { Text(filter.year.toString()) },
                    )
                }
                TextButton(onClick = onClear) { Text("Clear") }
            }
        }
        if (poems.isEmpty()) {
            EmptyState(Icons.Default.SearchOff, "No poems found", "Try changing your search or filters.")
        } else {
            PoemList(poems, onPoem)
        }
    }
    if (filtersOpen) {
        FilterSheet(
            authors = allPoems.map(Poem::author).distinct().sorted(),
            years = allPoems.mapNotNull(Poem::year).distinct().sortedDescending(),
            filter = filter,
            onFilter = onFilter,
            onDismiss = { filtersOpen = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(
    authors: List<String>,
    years: List<Int>,
    filter: PoemFilter,
    onFilter: ((PoemFilter) -> PoemFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Filter & sort", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Author", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(filter.author.isBlank(), { onFilter { it.copy(author = "") } }, { Text("All") })
                authors.forEach { author ->
                    FilterChip(
                        filter.author == author,
                        { onFilter { it.copy(author = author) } },
                        { Text(author) },
                    )
                }
            }
            Text("Year", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(filter.year == null, { onFilter { it.copy(year = null) } }, { Text("All") })
                years.forEach { year ->
                    FilterChip(
                        filter.year == year,
                        { onFilter { it.copy(year = year) } },
                        { Text(year.toString()) },
                    )
                }
            }
            Text("Sort by", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PoemSort.entries.forEach { sort ->
                    FilterChip(
                        filter.sort == sort,
                        { onFilter { it.copy(sort = sort) } },
                        { Text(sort.name.lowercase().replaceFirstChar(Char::uppercase)) },
                    )
                }
            }
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Show results") }
        }
    }
}

@Composable
fun FavoritesScreen(
    poems: List<Poem>,
    onPoem: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Text(
            "Favorites",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp),
        )
        if (poems.isEmpty()) {
            EmptyState(
                Icons.Default.FavoriteBorder,
                "No favorites yet",
                "Tap the heart on a poem to keep it close.",
            )
        } else {
            PoemList(poems, onPoem)
        }
    }
}

@Composable
private fun PoemList(
    poems: List<Poem>,
    onPoem: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(poems, key = Poem::id) { poem ->
            Card(onClick = { onPoem(poem.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f)) {
                            Text(poem.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                listOfNotNull(poem.author, poem.year?.toString()).joinToString(" · "),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        if (poem.isFavorite) {
                            Icon(
                                Icons.Default.Favorite,
                                "Favorite",
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        poem.content,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Serif,
                    )
                }
            }
        }
    }
}

@Composable
fun PoemDetailsScreen(
    poem: Poem,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onFavorite) {
                Icon(if (poem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite")
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
            IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Default.DeleteOutline, "Delete") }
        }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(poem.title, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(poem.author, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            poem.year?.let { Text(it.toString(), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            HorizontalDivider(Modifier.padding(vertical = 24.dp))
            Text(
                poem.content,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Serif,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.35f,
            )
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete this poem?") },
            text = { Text("This cannot be undone.") },
            confirmButton = { TextButton(onClick = onDelete) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
        )
    }
}

@Composable
fun PoemEditorScreen(
    poem: Poem?,
    onBack: () -> Unit,
    onSave: (Poem) -> Unit,
) {
    var title by remember(poem) { mutableStateOf(poem?.title.orEmpty()) }
    var author by remember(poem) { mutableStateOf(poem?.author.orEmpty()) }
    var year by remember(poem) { mutableStateOf(poem?.year?.toString().orEmpty()) }
    var content by remember(poem) { mutableStateOf(poem?.content.orEmpty()) }
    var attempted by remember(poem) { mutableStateOf(false) }
    val valid =
        title.isNotBlank() && author.isNotBlank() && content.isNotBlank() &&
            (year.isBlank() || year.toIntOrNull() != null)
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.Close, "Close") }
            Text(
                if (poem == null) "Add poem" else "Edit poem",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                attempted = true
                if (valid) {
                    onSave(
                        (
                            poem ?: Poem(
                                title = "",
                                author = "",
                                year = null,
                                content = "",
                            )
                        ).copy(title = title, author = author, year = year.toIntOrNull(), content = content),
                    )
                }
            }) { Text("Save") }
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                title,
                { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = attempted && title.isBlank(),
            )
            OutlinedTextField(
                author,
                { author = it },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = attempted && author.isBlank(),
            )
            OutlinedTextField(
                year,
                { year = it.filter(Char::isDigit).take(4) },
                label = { Text("Year (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                content,
                { content = it },
                label = { Text("Poem") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp),
                isError = attempted && content.isBlank(),
                placeholder = { Text("Write or paste the poem here…") },
            )
            if (attempted && !valid) {
                Text(
                    "Please add a title, author, and poem.",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
        if (action != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = 12.dp),
            ) { Text(action) }
        }
    }
}
