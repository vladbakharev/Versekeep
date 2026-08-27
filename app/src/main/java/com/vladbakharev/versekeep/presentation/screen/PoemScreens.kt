package com.vladbakharev.versekeep.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp as textSp
import com.vladbakharev.versekeep.R
import com.vladbakharev.versekeep.domain.model.Poem
import com.vladbakharev.versekeep.domain.model.PoemFilter
import com.vladbakharev.versekeep.domain.model.PoemSort
import com.vladbakharev.versekeep.presentation.theme.VersekeepTheme

private val CormorantGaramond =
    FontFamily(Font(R.font.cormorant_garamond, weight = FontWeight.Medium))

private val CormorantGaramondItalic =
    FontFamily(
        Font(
            R.font.cormorant_garamond_italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
        ),
    )

private val GnuTypewriter =
    FontFamily(Font(R.font.gnu_typewriter, weight = FontWeight.Normal))

@Composable
private fun ScreenTitle(
    text: String,
    topPadding: Dp = 24.dp,
) {
    Text(
        text = text,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = topPadding, bottom = 16.dp),
        style = MaterialTheme.typography.headlineLarge,
        fontSize = 36.textSp,
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun HomeScreen(
    poems: List<Poem>,
    onPoem: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (poems.isEmpty()) {
            ScreenTitle(stringResource(R.string.app_name))
            Box(Modifier
                .fillMaxWidth()
                .weight(1f)) {
                EmptyState(
                    Icons.Default.AutoStories,
                    stringResource(R.string.empty_collection_title),
                    stringResource(R.string.empty_collection_body),
                    stringResource(R.string.add_first_poem),
                    onAdd,
                )
            }
        } else {
            PoemList(
                poems = poems,
                onPoem = onPoem,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp,
                ),
                headerContent = {
                    item(key = "screen_title", span = { GridItemSpan(maxLineSpan) }) {
                        ScreenTitle(stringResource(R.string.app_name), topPadding = 8.dp)
                    }
                },
            )

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
    val active =
        filter.author.isNotBlank() || filter.year != null || filter.sort != PoemSort.RECENT

    @Composable
    fun LibraryControls(horizontalPadding: Dp) {
        OutlinedTextField(
            value = filter.query,
            onValueChange = { query -> onFilter { it.copy(query = query) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                IconButton(onClick = { filtersOpen = true }) {
                    Icon(Icons.Default.Tune, stringResource(R.string.filters))
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
            ),
        )
        if (active) {
            Row(
                Modifier.padding(horizontal = horizontalPadding, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (filter.author.isNotBlank()) {
                    InputChip(true, { onFilter { it.copy(author = "") } }, { Text(filter.author) })
                }
                if (filter.year != null) {
                    InputChip(
                        true,
                        { onFilter { it.copy(year = null) } },
                        { Text(filter.year.toString()) })
                }
                TextButton(onClick = onClear) { Text(stringResource(R.string.clear)) }
            }
        }
    }

    if (poems.isEmpty()) {
        Column(Modifier.fillMaxSize()) {
            ScreenTitle(stringResource(R.string.nav_library))
            LibraryControls(horizontalPadding = 20.dp)
            Box(Modifier.weight(1f)) {
                EmptyState(
                    Icons.Default.SearchOff,
                    stringResource(R.string.no_poems_found),
                    stringResource(R.string.no_poems_found_body),
                )
            }
        }
    } else {
        PoemList(
            poems = poems,
            onPoem = onPoem,
            headerContent = {
                item(key = "screen_title", span = { GridItemSpan(maxLineSpan) }) {
                    ScreenTitle(stringResource(R.string.nav_library), topPadding = 16.dp)
                }
                item(key = "library_controls", span = { GridItemSpan(maxLineSpan) }) {
                    LibraryControls(horizontalPadding = 0.dp)
                }
            },
        )
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
            Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                stringResource(R.string.filter_and_sort),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(stringResource(R.string.author), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    filter.author.isBlank(),
                    { onFilter { it.copy(author = "") } },
                    { Text(stringResource(R.string.all)) })
                authors.forEach { author ->
                    FilterChip(
                        filter.author == author,
                        { onFilter { it.copy(author = author) } },
                        { Text(author) },
                    )
                }
            }
            Text(stringResource(R.string.year), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    filter.year == null,
                    { onFilter { it.copy(year = null) } },
                    { Text(stringResource(R.string.all)) })
                years.forEach { year ->
                    FilterChip(
                        filter.year == year,
                        { onFilter { it.copy(year = year) } },
                        { Text(year.toString()) },
                    )
                }
            }
            Text(stringResource(R.string.sort_by), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PoemSort.entries.forEach { sort ->
                    FilterChip(
                        filter.sort == sort,
                        { onFilter { it.copy(sort = sort) } },
                        {
                            Text(
                                stringResource(
                                    when (sort) {
                                        PoemSort.RECENT -> R.string.sort_recent
                                        PoemSort.TITLE -> R.string.sort_title
                                        PoemSort.AUTHOR -> R.string.sort_author
                                        PoemSort.YEAR -> R.string.sort_year
                                    },
                                ),
                            )
                        },
                    )
                }
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.show_results)) }
        }
    }
}

@Composable
fun FavoritesScreen(
    poems: List<Poem>,
    onPoem: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (poems.isEmpty()) {
            ScreenTitle(stringResource(R.string.nav_favorites))
            Box(Modifier
                .fillMaxWidth()
                .weight(1f)) {
                EmptyState(
                    Icons.Default.FavoriteBorder,
                    stringResource(R.string.no_favorites),
                    stringResource(R.string.no_favorites_body),
                )
            }
        } else {
            PoemList(
                poems = poems,
                onPoem = onPoem,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp,
                ),
                headerContent = {
                    item(key = "screen_title", span = { GridItemSpan(maxLineSpan) }) {
                        ScreenTitle(stringResource(R.string.nav_favorites), topPadding = 8.dp)
                    }
                },
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(stringResource(R.string.nav_profile))
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PoemList(
    poems: List<Poem>,
    onPoem: (Long) -> Unit,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    headerContent: LazyGridScope.() -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        headerContent()
        itemsIndexed(
            items = poems,
            key = { _, poem -> poem.id },
        ) { index, poem ->
            val positionFromBottom = poems.lastIndex - index
            val isBlackCard = positionFromBottom % 3 == 0
            val cardColor =
                if (isBlackCard) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.background
            val cardContentColor =
                if (isBlackCard) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onBackground
            Card(
                onClick = { onPoem(poem.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor,
                    contentColor = cardContentColor,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            poem.title,
                            modifier = Modifier
                                .weight(1f)
                                .basicMarquee(iterations = Int.MAX_VALUE),
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 20.textSp,
                            fontFamily = CormorantGaramond,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                        )
                        if (poem.isFavorite) {
                            Icon(
                                painter = painterResource(R.drawable.favorites_button),
                                contentDescription = stringResource(R.string.favorite),
                                modifier = Modifier
                                    .padding(start = 4.dp, top = 4.dp)
                                    .size(20.dp),
                                tint = cardContentColor,
                            )
                        }
                    }
                    Text(
                        listOfNotNull(poem.author, poem.year?.toString()).joinToString(
                            stringResource(R.string.poem_metadata_separator)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee(iterations = Int.MAX_VALUE),
                        fontSize = 18.textSp,
                        fontFamily = CormorantGaramondItalic,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = cardContentColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        poem.content,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 14.textSp,
                        fontFamily = GnuTypewriter,
                        letterSpacing = (-1).textSp,
                        lineHeight = 18.textSp,
                    )
                }
            }
        }
        item(
            key = "bottom_bar_spacer",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Spacer(
                Modifier
                    .height(72.dp)
                    .navigationBarsPadding(),
            )
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
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(R.string.back)
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onFavorite) {
                Icon(
                    painter = painterResource(R.drawable.favorites_button),
                    contentDescription = stringResource(R.string.favorite),
                )
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, stringResource(R.string.edit)) }
            IconButton(onClick = { confirmDelete = true }) {
                Icon(
                    painter = painterResource(R.drawable.delete_button),
                    contentDescription = stringResource(R.string.delete),
                )
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                poem.title,
                style = MaterialTheme.typography.displaySmall,
                fontFamily = CormorantGaramond,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                poem.author,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.textSp,
                fontFamily = CormorantGaramondItalic,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
            poem.year?.let {
                Text(
                    it.toString(),
                    fontSize = 18.textSp,
                    fontFamily = CormorantGaramondItalic,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 24.dp))
            Text(
                poem.content,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.textSp,
                fontFamily = GnuTypewriter,
                letterSpacing = (-1).textSp,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.25f,
            )
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_poem_title)) },
            text = { Text(stringResource(R.string.delete_poem_body)) },
            confirmButton = { TextButton(onClick = onDelete) { Text(stringResource(R.string.delete)) } },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(
                        stringResource(
                            R.string.cancel
                        )
                    )
                }
            },
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
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.Close,
                    stringResource(R.string.close)
                )
            }
            Text(
                if (poem == null) stringResource(R.string.add_poem) else stringResource(R.string.edit_poem),
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
                                ).copy(
                                title = title,
                                author = author,
                                year = year.toIntOrNull(),
                                content = content
                            ),
                    )
                }
            }) { Text(stringResource(R.string.save)) }
        }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                title,
                { title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = attempted && title.isBlank(),
            )
            OutlinedTextField(
                author,
                { author = it },
                label = { Text(stringResource(R.string.author)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = attempted && author.isBlank(),
            )
            OutlinedTextField(
                year,
                { year = it.filter(Char::isDigit).take(4) },
                label = { Text(stringResource(R.string.year_optional)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                content,
                { content = it },
                label = { Text(stringResource(R.string.poem)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                isError = attempted && content.isBlank(),
                placeholder = { Text(stringResource(R.string.poem_hint)) },
            )
            if (attempted && !valid) {
                Text(
                    stringResource(R.string.poem_validation_error),
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
    onPrimaryBackground: Boolean = false,
) {
    val iconColor =
        if (onPrimaryBackground) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    val bodyColor =
        if (onPrimaryBackground) {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, modifier = Modifier.size(64.dp), tint = iconColor)
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(body, color = bodyColor, modifier = Modifier.padding(vertical = 8.dp))
        if (action != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = 12.dp),
                colors =
                    if (onPrimaryBackground) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    },
            ) { Text(action) }
        }
    }
}

private val previewPoems =
    listOf(
        Poem(
            id = 1L,
            title = "Hope is the thing with feathers",
            author = "Emily Dickinson",
            year = 1891,
            content =
                "“Hope” is the thing with feathers –\n" +
                        "That perches in the soul –\n" +
                        "And sings the tune without the words –\n" +
                        "And never stops – at all –",
            isFavorite = true,
        ),
        Poem(
            id = 2L,
            title = "The Road Not Taken",
            author = "Robert Frost",
            year = 1915,
            content =
                "Two roads diverged in a yellow wood,\n" +
                        "And sorry I could not travel both\n" +
                        "And be one traveler, long I stood",
        ),
    )

@Preview(name = "Home", showBackground = true)
@Composable
private fun HomeScreenPreview() {
    VersekeepTheme {
        HomeScreen(
            poems = previewPoems,
            onPoem = {},
            onAdd = {},
        )
    }
}

@Preview(name = "Library", showBackground = true)
@Composable
private fun LibraryScreenPreview() {
    VersekeepTheme {
        LibraryScreen(
            poems = previewPoems,
            allPoems = previewPoems,
            filter = PoemFilter(),
            onFilter = {},
            onClear = {},
            onPoem = {},
        )
    }
}

@Preview(name = "Favorites", showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    VersekeepTheme {
        FavoritesScreen(
            poems = previewPoems.filter(Poem::isFavorite),
            onPoem = {},
        )
    }
}

@Preview(name = "Poem details", showBackground = true)
@Composable
private fun PoemDetailsScreenPreview() {
    VersekeepTheme {
        PoemDetailsScreen(
            poem = previewPoems.first(),
            onBack = {},
            onEdit = {},
            onFavorite = {},
            onDelete = {},
        )
    }
}

@Preview(name = "Profile", showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    VersekeepTheme {
        ProfileScreen()
    }
}

@Preview(name = "Poem editor", showBackground = true)
@Composable
private fun PoemEditorScreenPreview() {
    VersekeepTheme {
        PoemEditorScreen(
            poem = previewPoems.first(),
            onBack = {},
            onSave = {},
        )
    }
}
