package com.example.youtubeforstudents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.webkit.WebView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubePlayerScreen(
    sectionDurationSeconds: Int?,
    showSectionControls: Boolean,
    isSectionComplete: Boolean,
    currentSection: Int,
    webViewRef: WebView?,
    onSectionComplete: () -> Unit,
    onPlayNextSection: () -> Unit,
    onRestartVideo: () -> Unit,
    onReplaySection: () -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    onSettingsClick: () -> Unit,
    onVideoChange: () -> Unit
) {
    var currentVideoId by remember { mutableStateOf("dQw4w9WgXcQ") } // Default video
    var videoTitle by remember { mutableStateOf("Rick Astley - Never Gonna Give You Up") }
    var searchQuery by remember { mutableStateOf("") }
    var customVideoId by remember { mutableStateOf("") }
    
    val sampleVideos =
        remember {
            listOf(
                VideoItem(
                    id = "dQw4w9WgXcQ",
                    title = "Rick Astley - Never Gonna Give You Up",
                    description = "The classic Rick Roll video"
                ),
                VideoItem(
                    id = "9bZkp7q19f0",
                    title = "PSY - GANGNAM STYLE",
                    description = "The viral K-pop sensation"
                ),
                VideoItem(
                    id = "kJQP7q19f0",
                    title = "Luis Fonsi - Despacito ft. Daddy Yankee",
                    description = "The most viewed video on YouTube"
                ),
                VideoItem(
                    id = "ZZ5LpwO-An4",
                    title = "Ylvis - The Fox (What Does The Fox Say?)",
                    description = "The catchy fox song"
                ),
                VideoItem(
                    id = "kJQP7q19f0",
                    title = "Luis Fonsi - Despacito ft. Daddy Yankee",
                    description = "The most viewed video on YouTube"
                ),
                VideoItem(
                    id = "ZZ5LpwO-An4",
                    title = "Ylvis - The Fox (What Does The Fox Say?)",
                    description = "The catchy fox song"
                ),
                VideoItem(
                    id = "dQw4w9WgXcQ",
                    title = "Rick Astley - Never Gonna Give You Up",
                    description = "The classic Rick Roll video"
                ),
                VideoItem(
                    id = "9bZkp7q19f0",
                    title = "PSY - GANGNAM STYLE",
                    description = "The viral K-pop sensation"
                )
            )
        }
    
    val filteredVideos =
        remember(searchQuery, sampleVideos) {
            if (searchQuery.isBlank()) {
                sampleVideos
            } else {
                sampleVideos.filter { video ->
                    video.title.contains(searchQuery, ignoreCase = true) ||
                        video.description.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    
    // Helper function to convert seconds to readable duration
    fun secondsToReadableDuration(seconds: Int): String {
        return when {
            seconds < 60 -> "$seconds second${if (seconds != 1) "s" else ""}"
            seconds < 3600 -> {
                val minutes = seconds / 60
                "$minutes minute${if (minutes != 1) "s" else ""}"
            }
            else -> {
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                "$hours hour${if (hours != 1) "s" else ""} $minutes minute${if (minutes != 1) "s" else ""}"
            }
        }
    }
    
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        // Header with Settings Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "YouTube for Students",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Player
        YouTubePlayer(
            videoId = currentVideoId,
            sectionDurationSeconds = sectionDurationSeconds,
            onSectionComplete = onSectionComplete,
            onPlayNextSection = onPlayNextSection,
            onWebViewCreated = onWebViewCreated,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Current Video Title
        Text(
            text = videoTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Section Status (only show if section mode is enabled)
        if (showSectionControls && sectionDurationSeconds != null) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSectionComplete) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Section $currentSection",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = "•",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = secondsToReadableDuration(sectionDurationSeconds!!),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (isSectionComplete) {
                            Text(
                                text = "Complete",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    if (isSectionComplete) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onPlayNextSection,
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                            ) {
                                Text("Next", fontSize = 12.sp)
                            }
                            
                            OutlinedButton(
                                onClick = onReplaySection,
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                            ) {
                                Text("Replay", fontSize = 12.sp)
                            }
                            
                            OutlinedButton(
                                onClick = onRestartVideo,
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                            ) {
                                Text("Restart", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Custom Video Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customVideoId,
                onValueChange = { customVideoId = it },
                label = { Text("YouTube Video ID") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("e.g., dQw4w9WgXcQ") }
            )
            
            Button(
                onClick = {
                    if (customVideoId.isNotBlank()) {
                        currentVideoId = customVideoId
                        videoTitle = "Custom Video"
                        customVideoId = ""
                        onVideoChange()
                    }
                }
            ) {
                Text("Play")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search videos...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Video Selection
        Text(
            text = "Available Videos:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredVideos) { video ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        currentVideoId = video.id
                        videoTitle = video.title
                        onVideoChange()
                    }
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                    ) {
                        Text(
                            text = video.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
