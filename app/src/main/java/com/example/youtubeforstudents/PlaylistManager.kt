package com.example.youtubeforstudents

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "playlist_prefs"
        private const val KEY_PLAYLIST = "saved_playlist"
        private const val MAX_PLAYLIST_SIZE = 5
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Add a video to the playlist
     * If the video already exists, it will be moved to the top
     * If playlist is full, the oldest video will be removed
     */
    suspend fun addToPlaylist(video: VideoItem): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentPlaylist = loadPlaylist()
            val existingIndex = currentPlaylist.items.indexOfFirst { it.videoId == video.id }
            
            val newPlaylistItem = PlaylistItem(
                videoId = video.id,
                title = video.title,
                description = video.description
            )
            
            val newItems = if (existingIndex != -1) {
                // Video already exists, move to top
                val items = currentPlaylist.items.toMutableList()
                items.removeAt(existingIndex)
                listOf(newPlaylistItem) + items
            } else {
                // Add new video to top
                val items = listOf(newPlaylistItem) + currentPlaylist.items
                // Remove oldest if exceeding max size
                if (items.size > MAX_PLAYLIST_SIZE) {
                    items.take(MAX_PLAYLIST_SIZE)
                } else {
                    items
                }
            }
            
            val updatedPlaylist = currentPlaylist.copy(items = newItems)
            savePlaylist(updatedPlaylist)
            true
        } catch (e: Exception) {
            android.util.Log.e("PlaylistManager", "Error adding to playlist", e)
            false
        }
    }
    
    /**
     * Update the resume position for a specific video
     */
    suspend fun updateResumePosition(videoId: String, position: Double): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentPlaylist = loadPlaylist()
            val updatedItems = currentPlaylist.items.map { item ->
                if (item.videoId == videoId) {
                    item.copy(resumePosition = position)
                } else {
                    item
                }
            }
            
            val updatedPlaylist = currentPlaylist.copy(items = updatedItems)
            savePlaylist(updatedPlaylist)
            true
        } catch (e: Exception) {
            android.util.Log.e("PlaylistManager", "Error updating resume position", e)
            false
        }
    }
    
    /**
     * Remove a video from the playlist
     */
    suspend fun removeFromPlaylist(videoId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentPlaylist = loadPlaylist()
            val updatedItems = currentPlaylist.items.filter { it.videoId != videoId }
            val updatedPlaylist = currentPlaylist.copy(items = updatedItems)
            savePlaylist(updatedPlaylist)
            true
        } catch (e: Exception) {
            android.util.Log.e("PlaylistManager", "Error removing from playlist", e)
            false
        }
    }
    
    /**
     * Get the current playlist
     */
    suspend fun getPlaylist(): Playlist = withContext(Dispatchers.IO) {
        loadPlaylist()
    }
    
    /**
     * Check if a video is in the playlist
     */
    suspend fun isInPlaylist(videoId: String): Boolean = withContext(Dispatchers.IO) {
        val playlist = loadPlaylist()
        playlist.items.any { it.videoId == videoId }
    }
    
    /**
     * Get resume position for a specific video
     */
    suspend fun getResumePosition(videoId: String): Double = withContext(Dispatchers.IO) {
        val playlist = loadPlaylist()
        playlist.items.find { it.videoId == videoId }?.resumePosition ?: 0.0
    }
    
    /**
     * Clear the entire playlist
     */
    suspend fun clearPlaylist(): Boolean = withContext(Dispatchers.IO) {
        try {
            val emptyPlaylist = Playlist()
            savePlaylist(emptyPlaylist)
            true
        } catch (e: Exception) {
            android.util.Log.e("PlaylistManager", "Error clearing playlist", e)
            false
        }
    }
    
    private fun savePlaylist(playlist: Playlist) {
        try {
            val jsonString = json.encodeToString(playlist)
            prefs.edit().putString(KEY_PLAYLIST, jsonString).apply()
        } catch (e: Exception) {
            android.util.Log.e("PlaylistManager", "Error saving playlist", e)
        }
    }
    
    private fun loadPlaylist(): Playlist {
        return try {
            val jsonString = prefs.getString(KEY_PLAYLIST, null)
            if (jsonString != null) {
                json.decodeFromString<Playlist>(jsonString)
            } else {
                Playlist()
            }
        } catch (e: Exception) {
            android.util.Log.e("PlaylistManager", "Error loading playlist", e)
            Playlist()
        }
    }
} 