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

data class VideoItem(
    val id: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubePlayerScreen() {
    var currentVideoId by remember { mutableStateOf("dQw4w9WgXcQ") } // Default video
    var videoTitle by remember { mutableStateOf("Rick Astley - Never Gonna Give You Up") }
    var searchQuery by remember { mutableStateOf("") }
    var customVideoId by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    
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
    
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        // App Title
        Text(
            text = "YouTube for Students",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Video Player
        YouTubePlayer(
            videoId = currentVideoId,
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
