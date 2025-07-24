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
                        title = "How to Invest for Beginners",
                        description = "Learn the basics of investing and how to start building wealth"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "Stock Market Investing 101",
                        description = "Complete guide to stock market investing for students"
                    ),
                    VideoItem(
                        id = "jNQXAC9IVRw",
                        title = "How the Stock Market Works",
                        description = "Understanding how the stock market and investing works"
                    )
                )
                query.contains("cook", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "Cooking Basics for Beginners",
                        description = "Learn essential cooking techniques and recipes"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "Quick and Easy Recipes",
                        description = "Simple recipes you can make in 30 minutes or less"
                    )
                )
                query.contains("program", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "jNQXAC9IVRw",
                        title = "Programming for Beginners",
                        description = "Learn to code from scratch with this comprehensive guide"
                    ),
                    VideoItem(
                        id = "kJQP7q19f0",
                        title = "Python Programming Tutorial",
                        description = "Complete Python programming course for beginners"
                    )
                )
                query.contains("music", ignoreCase = true) -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "Music Theory Basics",
                        description = "Learn the fundamentals of music theory"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "Guitar Lessons for Beginners",
                        description = "Start learning guitar with these easy lessons"
                    )
                )
                else -> listOf(
                    VideoItem(
                        id = "dQw4w9WgXcQ",
                        title = "Rick Astley - Never Gonna Give You Up",
                        description = "The classic Rick Roll video - search result for '$query'"
                    ),
                    VideoItem(
                        id = "9bZkp7q19f0",
                        title = "PSY - GANGNAM STYLE",
                        description = "The viral K-pop sensation - search result for '$query'"
                    ),
                    VideoItem(
                        id = "jNQXAC9IVRw",
                        title = "Me at the zoo",
                        description = "The first video ever uploaded to YouTube - search result for '$query'"
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
        
        // API Status Indicator
        if (!YouTubeConfig.isRealApiConfigured()) {
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
                        text = "⚠️ Using mock search",
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
        
        // Search Status
        if (isSearching) {
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
        
        if (searchError != null) {
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
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videosToShow) { video ->
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
