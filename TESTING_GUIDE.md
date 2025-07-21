# Testing Guide for Section Control

## How to Test Section Mode

### 1. Enable Section Mode
1. Open the app
2. Tap the **Settings** button (gear icon) in the top-right corner
3. Toggle "Enable Section Mode" to **ON**
4. Enter a small number like **10** in the Duration field
5. Select **Seconds** (for quick testing)
6. Tap the back button to return to the main screen

### 2. Verify Section Mode is Active
- You should see a section status card below the video title
- It should show "Section Mode: ENABLED"
- It should show "Duration: 10 seconds"
- It should display "Section 1"

### 3. Test Section Control
1. Play any video from the list
2. The video should automatically pause after 10 seconds
3. You should see "Section complete! Ready for next section."
4. The section number should remain at 1
5. Two buttons should appear: "Play Next Section" and "Restart"

### 4. Test Next Section
1. Tap "Next Section"
2. The video should resume from where it left off
3. It should play for another 10 seconds and pause again
4. The section number should increment to 2

### 5. Test Replay Section
1. After a section completes, tap "Replay"
2. The video should go back to the start of the current section
3. It should play the same section again and pause after the duration
4. The section number should remain the same

### 6. Test Restart
1. Tap "Restart"
2. The video should go back to the beginning
3. The section number should reset to 1
4. It should play for 10 seconds and pause

## Debugging Information

### Console Logs
Check the Android Logcat for messages starting with "YouTubePlayer: Console:" to see:
- "YouTube API Ready" - API loaded successfully
- "Player ready" - Player initialized
- "Player state changed: X" - Player state changes
- "Starting section timer for X seconds" - Timer started
- "Current time: X Section end: Y" - Time tracking
- "Section complete, pausing video" - Section completed

### Visual Indicators
- Section status card should appear when mode is enabled
- Debug info shows "Section Mode: ENABLED"
- Duration shows the correct number of seconds
- Section number increments with each section

## Common Issues and Solutions

### Issue: Video plays entirely without pausing
**Possible Causes:**
- Section mode not enabled
- Duration not set correctly
- JavaScript not loading properly

**Solutions:**
1. Verify section mode is enabled in settings
2. Check that duration is a positive number
3. Look for console logs in Logcat
4. Try a shorter duration (5-10 seconds) for testing

### Issue: Section controls don't appear
**Possible Causes:**
- Section mode disabled
- Duration not set

**Solutions:**
1. Go to settings and enable section mode
2. Set a duration value
3. Return to main screen

### Issue: "Play Next Section" doesn't work
**Possible Causes:**
- JavaScript interface not properly connected
- Player not ready

**Solutions:**
1. Check console logs for errors
2. Try restarting the video first
3. Ensure video is loaded before testing

## Expected Behavior

### When Section Mode is Enabled:
- Video automatically pauses after specified duration
- Section status card appears with current section info
- "Next Section", "Replay", and "Restart" buttons appear after pause
- Section number increments with each new section
- Replay button allows reviewing the current section from its start

### When Section Mode is Disabled:
- Video plays normally without interruption
- No section status card appears
- No automatic pausing occurs

## Testing Tips

1. **Start with short durations** (5-10 seconds) for quick testing
2. **Use different videos** to ensure it works with various content
3. **Check console logs** for debugging information
4. **Test both seconds and minutes** to verify time unit selection
5. **Verify state persistence** by switching between screens

## Logcat Commands

To view console logs in Android Studio:
1. Open Logcat in Android Studio
2. Filter by "YouTubePlayer"
3. Look for console messages from the WebView

Or use adb command:
```bash
adb logcat | grep "YouTubePlayer"
``` 