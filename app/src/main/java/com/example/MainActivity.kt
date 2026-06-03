package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.WallpaperRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WallpaperViewModel
import com.example.ui.viewmodel.WallpaperViewModelFactory
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup daily uploads notification channel
        NotificationHelper.createNotificationChannel(this)
        
        // Dynamic Room DB setup following repository pattern
        val database = AppDatabase.getDatabase(this)
        val repository = WallpaperRepository(database.wallpaperDao())
        
        // ViewModel instantiation via clean factory pattern
        val factory = WallpaperViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[WallpaperViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            // Reactive dark mode system mapping from settings
            val darkThemeSetting by viewModel.darkThemeSetting.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val applyDarkTheme = when (darkThemeSetting) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme
            }

            MyApplicationTheme(darkTheme = applyDarkTheme) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
