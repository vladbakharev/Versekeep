package com.vladbakharev.versekeep

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.vladbakharev.versekeep.presentation.VersekeepApp
import com.vladbakharev.versekeep.presentation.VersekeepViewModel
import com.vladbakharev.versekeep.presentation.theme.VersekeepTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: VersekeepViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val preferences = remember {
                getSharedPreferences("versekeep_preferences", MODE_PRIVATE)
            }
            var darkTheme by remember {
                mutableStateOf(preferences.getBoolean("dark_theme", false))
            }
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle =
                        if (darkTheme) SystemBarStyle.dark(Color.TRANSPARENT)
                        else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                    navigationBarStyle =
                        if (darkTheme) SystemBarStyle.dark(Color.TRANSPARENT)
                        else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                )
            }
            VersekeepTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                VersekeepApp(
                    navController = navController,
                    viewModel = viewModel,
                    darkTheme = darkTheme,
                    onDarkThemeChange = { enabled ->
                        darkTheme = enabled
                        preferences.edit().putBoolean("dark_theme", enabled).apply()
                    },
                )
            }
        }
    }
}
