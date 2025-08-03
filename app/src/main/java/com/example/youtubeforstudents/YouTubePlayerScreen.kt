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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    var currentVideoId by remember { mutableStateOf("dQw4w9WgXcQ") }
    var videoTitle by remember { mutableStateOf("Rick Astley - Never Gonna Give You Up") }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    
    // Get YouTube API key from config
    val youtubeApiKey = YouTubeConfig.getApiKey()
    
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
                    id = "jNQXAC9IVRw",
                    title = "Me at the zoo",
                    description = "The first video ever uploaded to YouTube"
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
                )
            )
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

    // Function to search YouTube using real API
    fun searchYouTube(query: String) {
        if (query.isBlank()) {
            searchResults = emptyList()
            searchError = null
            return
        }
        
        isSearching = true
        searchError = null
        
        // Use real YouTube API if configured, otherwise fall back to mock
        if (YouTubeConfig.isRealApiConfigured()) {
            android.util.Log.d("YouTubeSearch", "Using real YouTube API for search: '$query'")
            NetworkModule.youtubeViewModel.searchVideos(query, youtubeApiKey!!) { results, error ->
                isSearching = false
                if (error != null) {
                    searchError = "Search failed: $error"
                    android.util.Log.e("YouTubeSearch", "API Error: $error")
                } else {
                    searchResults = results
                    android.util.Log.d("YouTubeSearch", "Found ${results.size} videos for '$query'")
                }
            }
        } else {
            // Fallback to mock search for development/testing
            android.util.Log.w("YouTubeSearch", "Using mock search - configure real API key for production")
            
            val mockResults = when {
                query.contains("invest", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "How to Invest for Beginners - Complete Guide",
                        description = "Learn the basics of investing and how to start building wealth with this comprehensive tutorial"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "Stock Market Investing 101 - Full Course",
                        description = "Complete guide to stock market investing for students and beginners"
                    ),
                    VideoItem(
                        id = "jNQXAC9IVRw",
                        title = "How the Stock Market Works - Educational Video",
                        description = "Understanding how the stock market and investing works in detail"
                    )
                )
                query.contains("cook", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "Cooking Basics for Beginners - Complete Tutorial",
                        description = "Learn essential cooking techniques and recipes from scratch"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "Quick and Easy Recipes - Full Cooking Guide",
                        description = "Simple recipes you can make in 30 minutes or less with detailed instructions"
                    )
                )
                query.contains("program", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "jNQXAC9IVRw",
                        title = "Programming for Beginners - Complete Course",
                        description = "Learn to code from scratch with this comprehensive programming guide"
                    ),
                    VideoItem(
                        id = "kJQP7q19f0",
                        title = "Python Programming Tutorial - Full Beginner Course",
                        description = "Complete Python programming course for beginners with hands-on projects"
                    )
                )
                query.contains("music", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "Music Theory Basics - Complete Guide",
                        description = "Learn the fundamentals of music theory with comprehensive lessons"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "Guitar Lessons for Beginners - Full Course",
                        description = "Start learning guitar with these comprehensive easy lessons and tutorials"
                    )
                )
                else -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "Rick Astley - Never Gonna Give You Up (Full Song)",
                        description = "The classic Rick Roll video - educational content for '$query'"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "PSY - GANGNAM STYLE (Official Music Video)",
                        description = "The viral K-pop sensation - educational content for '$query'"
                    ),
                    VideoItem(
                        id = "jNQXAC9IVRw",
                        title = "Me at the zoo (First YouTube Video)",
                        description = "The first video ever uploaded to YouTube - educational content for '$query'"
                    )
                )
            }
            
            // Simulate API delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000) // Simulate network delay
                searchResults = mockResults
                isSearching = false
            }
        }
    }
    
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        item {
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
        }
        
        // Section Status (only show if section mode is enabled)
        if (showSectionControls && sectionDurationSeconds != null) {
            item {
                android.util.Log.d("YouTubePlayerScreen", "ironman: Rendering section controls - showSectionControls: $showSectionControls, sectionDurationSeconds: $sectionDurationSeconds")
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
                            
                            // Section completion indicator
                            Text(
                                text = "Section completed successfully!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Essential controls - always show when section mode is enabled
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Manual start button for testing
                        OutlinedButton(
                            onClick = {
                                webViewRef?.evaluateJavascript("window.startCountdownTimer();", null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Start Countdown Timer", fontSize = 10.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Force pause button (enhanced)
                        OutlinedButton(
                            onClick = {
                                webViewRef?.evaluateJavascript("window.forcePauseVideo();", null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Force Pause Video", fontSize = 10.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Section Navigation Controls
                        Text(
                            text = "Section Navigation:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Previous Section button
                            OutlinedButton(
                                onClick = {
                                    webViewRef?.evaluateJavascript("window.goToPreviousSection();", null)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Previous", fontSize = 10.sp)
                            }
                            
                            // Restart Current Section button
                            OutlinedButton(
                                onClick = {
                                    webViewRef?.evaluateJavascript("window.restartCurrentSection();", null)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Restart", fontSize = 10.sp)
                            }
                            
                            // Next Section button
                            OutlinedButton(
                                onClick = {
                                    webViewRef?.evaluateJavascript("window.goToNextSection();", null)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Next", fontSize = 10.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Section info display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Duration: ${sectionDurationSeconds}s",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "Use buttons to navigate",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search YouTube...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Button(
                    onClick = { searchYouTube(searchQuery) },
                    enabled = searchQuery.isNotBlank() && !isSearching
                ) {
                    Text("Search")
                }
            }
        }
        
        // API Status Indicator
        if (!YouTubeConfig.isRealApiConfigured()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Using mock data for development",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = "Configure API key for real search",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        
        // Search Status
        if (isSearching) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Searching YouTube for '$searchQuery'...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (searchError != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Search error: $searchError",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Video Selection - Show search results or sample videos
            val videosToShow = if (searchResults.isNotEmpty()) {
                searchResults
            } else {
                sampleVideos
            }
            
            val sectionTitle = if (searchResults.isNotEmpty()) {
                "Search Results for '$searchQuery':"
            } else {
                "Available Videos:"
            }
            
            Text(
                text = sectionTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(if (searchResults.isNotEmpty()) searchResults else sampleVideos) { video ->
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
