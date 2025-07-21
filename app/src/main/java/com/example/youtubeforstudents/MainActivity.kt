package com.example.youtubeforstudents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.youtubeforstudents.ui.theme.YoutubeForStudentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YoutubeForStudentsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("main") }
    
    // Section control state - shared between screens
    var sectionDurationNumber by remember { mutableStateOf("") }
    var selectedTimeUnit by remember { mutableStateOf(TimeUnit.MINUTES) }
    var sectionDurationSeconds by remember { mutableStateOf<Int?>(null) }
    var showSectionControls by remember { mutableStateOf(false) }
    var isSectionComplete by remember { mutableStateOf(false) }
    var currentSection by remember { mutableStateOf(1) }
    var webViewRef by remember { mutableStateOf<android.webkit.WebView?>(null) }
    
    // Helper function to convert number and time unit to seconds
    fun calculateSectionDurationSeconds(number: String, timeUnit: TimeUnit): Int? {
        return try {
            if (number.isBlank()) return null
            
            val numberValue = number.toInt()
            if (numberValue <= 0) return null
            
            when (timeUnit) {
                TimeUnit.SECONDS -> numberValue
                TimeUnit.MINUTES -> numberValue * 60
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
    
    // Update section duration when inputs change
    LaunchedEffect(sectionDurationNumber, selectedTimeUnit) {
        sectionDurationSeconds = calculateSectionDurationSeconds(sectionDurationNumber, selectedTimeUnit)
    }
    
    when (currentScreen) {
        "main" -> {
            YouTubePlayerScreen(
                sectionDurationSeconds = sectionDurationSeconds,
                showSectionControls = showSectionControls,
                isSectionComplete = isSectionComplete,
                currentSection = currentSection,
                webViewRef = webViewRef,
                onSectionComplete = {
                    isSectionComplete = true
                },
                onPlayNextSection = {
                    webViewRef?.evaluateJavascript("window.playNextSection();", null)
                    isSectionComplete = false
                    currentSection++
                },
                onRestartVideo = {
                    webViewRef?.evaluateJavascript("window.resetSectionMode();", null)
                    isSectionComplete = false
                    currentSection = 1
                },
                onWebViewCreated = { webView ->
                    webViewRef = webView
                },
                onSettingsClick = {
                    currentScreen = "settings"
                },
                onVideoChange = {
                    // Reset section state when changing videos
                    isSectionComplete = false
                    currentSection = 1
                }
            )
        }
        "settings" -> {
            SettingsScreen(
                sectionDurationNumber = sectionDurationNumber,
                onSectionDurationNumberChange = { sectionDurationNumber = it },
                selectedTimeUnit = selectedTimeUnit,
                onTimeUnitChange = { selectedTimeUnit = it },
                showSectionControls = showSectionControls,
                onShowSectionControlsChange = { 
                    showSectionControls = it
                    if (!it) {
                        sectionDurationSeconds = null
                        sectionDurationNumber = ""
                        isSectionComplete = false
                        currentSection = 1
                    }
                },
                sectionDurationSeconds = sectionDurationSeconds,
                onBackClick = {
                    currentScreen = "main"
                }
            )
        }
    }
}
