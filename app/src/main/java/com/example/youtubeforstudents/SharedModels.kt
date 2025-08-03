package com.example.youtubeforstudents

import kotlinx.serialization.Serializable

enum class TimeUnit {
    SECONDS, MINUTES
}

data class VideoItem(
    val id: String,
    val title: String,
    val description: String
)

@Serializable
data class PlaylistItem(
    val videoId: String,
    val title: String,
    val description: String,
    val resumePosition: Double = 0.0, // Position in seconds where user stopped
    val addedAt: Long = System.currentTimeMillis()
)

@Serializable
data class Playlist(
    val items: List<PlaylistItem> = emptyList(),
    val maxItems: Int = 5
) 