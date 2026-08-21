package com.vladbakharev.versekeep

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            VersekeepTheme {
                val navController = rememberNavController()
                VersekeepApp(
                    navController = navController,
                    viewModel = viewModel,
                )
            }
        }
    }
}
