package com.example.yra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.yra.ui.navigation.YraNavGraph
import com.example.yra.ui.theme.YraTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.yra.domain.repository.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = applicationContext as YraApplication
            val userPreferences by app.userPreferencesRepository.userPreferencesFlow.collectAsState(
                initial = com.example.yra.domain.repository.UserPreferences()
            )

            val isDarkTheme = when (userPreferences.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            YraTheme(
                darkTheme = isDarkTheme,
                fontScale = userPreferences.fontScale
            ) {
                YraNavGraph()
            }
        }
    }
}