package com.vladbakharev.versekeep.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.vladbakharev.versekeep.R
import com.vladbakharev.versekeep.presentation.navigation.Screen
import com.vladbakharev.versekeep.presentation.screen.FavoritesScreen
import com.vladbakharev.versekeep.presentation.screen.HomeScreen
import com.vladbakharev.versekeep.presentation.screen.LibraryScreen
import com.vladbakharev.versekeep.presentation.screen.PoemDetailsScreen
import com.vladbakharev.versekeep.presentation.screen.PoemEditorScreen
import com.vladbakharev.versekeep.presentation.screen.ProfileScreen

private data class BottomNavItem(
    val label: String,
    val route: String,
    @param:DrawableRes val drawableRes: Int,
)

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
    val isRoot = currentRoute in setOf(Screen.HOME, Screen.LIBRARY, Screen.FAVORITES, Screen.PROFILE)

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
                        .shadow(8.dp, RoundedCornerShape(32.dp), clip = false),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    NavigationBar(
                        modifier = Modifier.height(60.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                    ) {
                        listOf(
                            BottomNavItem(
                                label = stringResource(R.string.nav_home),
                                route = Screen.HOME,
                                drawableRes = R.drawable.home_button,
                            ),
                            BottomNavItem(
                                label = stringResource(R.string.nav_library),
                                route = Screen.LIBRARY,
                                drawableRes = R.drawable.library_button,
                            ),
                            BottomNavItem(
                                label = stringResource(R.string.nav_favorites),
                                route = Screen.FAVORITES,
                                drawableRes = R.drawable.favorites_button,
                            ),
                            BottomNavItem(
                                label = stringResource(R.string.nav_profile),
                                route = Screen.PROFILE,
                                drawableRes = R.drawable.profile_button,
                            ),
                        ).forEach { item ->
                            val label = item.label
                            val route = item.route
                            val selected = currentRoute == route
                            val contentColor =
                                if (selected) MaterialTheme.colorScheme.surface
                                else Color.Black
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(
                                    if (selected) Color.Black
                                        else Color.Transparent,
                                    )
                                    .clickable { navigateToRoot(route) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    painter = painterResource(item.drawableRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = contentColor,
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor,
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (isRoot && currentRoute != Screen.PROFILE) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.editor()) },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .border(0.5.dp, Color.White, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 8.dp,
                        focusedElevation = 8.dp,
                        hoveredElevation = 8.dp,
                    ),
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_poem))
                }
            }
        },
    ) { padding ->
        Box(
            Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize(),
        ) {
            NavHost(navController = navController, startDestination = Screen.HOME) {
                composable(Screen.HOME) {
                    HomeScreen(
                        poems = poems.sortedByDescending { it.createdAt },
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
                composable(Screen.PROFILE) {
                    ProfileScreen()
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
