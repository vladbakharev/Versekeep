package com.vladbakharev.versekeep.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.vladbakharev.versekeep.presentation.navigation.Screen
import com.vladbakharev.versekeep.presentation.screen.FavoritesScreen
import com.vladbakharev.versekeep.presentation.screen.HomeScreen
import com.vladbakharev.versekeep.presentation.screen.LibraryScreen
import com.vladbakharev.versekeep.presentation.screen.PoemDetailsScreen
import com.vladbakharev.versekeep.presentation.screen.PoemEditorScreen

@Composable
fun VersekeepApp(
    navController: NavHostController,
    viewModel: VersekeepViewModel,
) {
    val poems by viewModel.poems.collectAsStateWithLifecycle()
    val filtered by viewModel.filteredPoems.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isRoot = currentRoute in setOf(Screen.HOME, Screen.LIBRARY, Screen.FAVORITES)

    fun navigateToRoot(route: String) {
        navController.navigate(route) {
            popUpTo(Screen.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (isRoot) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 32.dp, vertical = 6.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), clip = false),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    NavigationBar(
                        modifier = Modifier.height(60.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                    ) {
                        listOf(
                            Triple("Home", Icons.Default.Home, Screen.HOME),
                            Triple("Library", Icons.AutoMirrored.Filled.MenuBook, Screen.LIBRARY),
                            Triple("Favorites", Icons.Default.Favorite, Screen.FAVORITES),
                        ).forEach { (label, icon, route) ->
                            NavigationBarItem(
                                modifier = Modifier.offset(y = 4.dp),
                                selected = currentRoute == route,
                                onClick = { navigateToRoot(route) },
                                icon = { Icon(icon, contentDescription = null) },
                                label = { Text(label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.surface,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (isRoot) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.editor()) },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add poem")
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            NavHost(navController = navController, startDestination = Screen.HOME) {
                composable(Screen.HOME) {
                    HomeScreen(
                        poems = poems.sortedByDescending { it.createdAt }.take(8),
                        onPoem = { navController.navigate(Screen.details(it)) },
                        onAdd = { navController.navigate(Screen.editor()) },
                    )
                }
                composable(Screen.LIBRARY) {
                    LibraryScreen(
                        poems = filtered,
                        allPoems = poems,
                        filter = filter,
                        onFilter = viewModel::updateFilter,
                        onClear = viewModel::clearFilter,
                        onPoem = { navController.navigate(Screen.details(it)) },
                    )
                }
                composable(Screen.FAVORITES) {
                    FavoritesScreen(
                        poems = poems.filter { it.isFavorite },
                        onPoem = { navController.navigate(Screen.details(it)) },
                    )
                }
                composable(
                    route = Screen.DETAILS,
                    arguments = listOf(navArgument("poemId") { type = NavType.LongType }),
                ) { entry ->
                    val poemId = entry.arguments?.getLong("poemId") ?: return@composable
                    poems.firstOrNull { it.id == poemId }?.let { poem ->
                        PoemDetailsScreen(
                            poem = poem,
                            onBack = { navController.popBackStack() },
                            onEdit = { navController.navigate(Screen.editor(poem.id)) },
                            onFavorite = { viewModel.toggleFavorite(poem.id) },
                            onDelete = {
                                viewModel.delete(poem.id)
                                navController.popBackStack()
                            },
                        )
                    }
                }
                composable(
                    route = Screen.EDITOR,
                    arguments = listOf(
                        navArgument("poemId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        },
                    ),
                ) { entry ->
                    val poemId = entry.arguments
                        ?.getLong("poemId")
                        ?.takeIf { it >= 0L }
                    PoemEditorScreen(
                        poem = poemId?.let(viewModel::find),
                        onBack = { navController.popBackStack() },
                        onSave = { draft ->
                            val savedId = viewModel.save(draft)
                            navController.navigate(Screen.details(savedId)) {
                                popUpTo(Screen.EDITOR) { inclusive = true }
                            }
                        },
                    )
                }
            }
        }
    }
}
