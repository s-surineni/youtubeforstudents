package com.example.youtubeforstudents

/**
 * YouTube API Configuration
 * 
 * To enable real YouTube search:
 * 1. Go to https://console.cloud.google.com/
 * 2. Create a new project or select existing one
 * 3. Enable YouTube Data API v3
 * 4. Create credentials (API Key)
 * 5. Add your API key to local.properties:
 *    YOUTUBE_API_KEY=your_actual_api_key_here
 * 
 * Note: local.properties is not committed to version control
 */
object YouTubeConfig {
    // Get API key from BuildConfig (set in local.properties)
    private val API_KEY = BuildConfig.YOUTUBE_API_KEY
    
    // Check if real API is configured
    fun isRealApiConfigured(): Boolean {
        return API_KEY.isNotBlank() && API_KEY != ""
    }
    
    // Get API key with validation
    fun getApiKey(): String? {
        return if (isRealApiConfigured()) API_KEY else null
    }
} 