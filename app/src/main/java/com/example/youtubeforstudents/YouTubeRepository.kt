package com.example.youtubeforstudents

class YouTubeRepository(private val apiService: YouTubeApiService) {
    suspend fun searchVideos(query: String, apiKey: String): List<VideoItem> {
        return try {
            val response = apiService.searchVideos(
                query = query,
                apiKey = apiKey
            )
            
            response.items.map { item ->
                VideoItem(
                    id = item.id.videoId,
                    title = item.snippet.title,
                    description = item.snippet.description
                )
            }
        } catch (e: Exception) {
            // Return empty list on error, but log the exception
            android.util.Log.e("YouTubeRepository", "Error searching videos: ${e.message}", e)
            emptyList()
        }
    }
} 