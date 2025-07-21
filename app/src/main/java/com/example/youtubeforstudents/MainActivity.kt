package com.example.youtubeforstudents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.youtubeforstudents.ui.theme.YoutubeForStudentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YoutubeForStudentsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    YouTubePlayerScreen()
                }
            }
        }
    }
}
