# YouTube for Students

An Android app that allows students to watch YouTube videos in manageable sections for focused learning sessions.

## 🎯 **Features**

- **YouTube Video Player**: Embedded YouTube player with full controls
- **Real YouTube Search**: Search actual YouTube content (requires API key)
- **Excludes YouTube Shorts**: Focuses on longer educational content
- **Section-Based Learning**: Play videos in timed sections for better learning
- **Settings Screen**: Configure section duration and learning preferences
- **Replay Section**: Replay current section from the beginning
- **Secure API Key Storage**: API keys stored securely in local.properties
- **Mock Search Fallback**: Works without API key for development/testing

## How to Use

### Basic Video Playback
1. Launch the app
2. Browse the available videos in the list
3. Tap any video to start playing
4. Use standard YouTube player controls for playback

### Section-Based Learning
1. Tap the **Settings** button (gear icon) in the top-right corner
2. Toggle "Enable Section Mode" to **ON**
3. Enter your desired section duration (e.g., 30 seconds or 2 minutes)
4. Select the time unit (seconds or minutes)
5. Return to the main screen
6. Play any video - it will automatically pause after each section
7. Use the section controls to:
   - **Next**: Continue to the next section
   - **Replay**: Review the current section from the beginning
   - **Restart**: Start the video from the beginning

### Video Search
- Use the search bar to filter videos by title or description
- The list updates in real-time as you type 