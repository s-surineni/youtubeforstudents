package com.example.youtubeforstudents

enum class TimeUnit {
    SECONDS, MINUTES
}

data class VideoItem(
    val id: String,
    val title: String,
    val description: String
) 