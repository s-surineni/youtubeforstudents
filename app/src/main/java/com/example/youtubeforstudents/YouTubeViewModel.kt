package com.example.youtubeforstudents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class YouTubeViewModel(
    private val repository: YouTubeRepository
) : ViewModel() {
    
    fun searchVideos(query: String, apiKey: String, callback: (List<VideoItem>, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val results = repository.searchVideos(query, apiKey)
                callback(results, null)
            } catch (e: Exception) {
                android.util.Log.e("YouTubeViewModel", "Error in searchVideos: ${e.message}", e)
                callback(emptyList(), e.message)
            }
        }
    }
} 