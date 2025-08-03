package com.example.youtubeforstudents

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.util.Log
import android.webkit.JavascriptInterface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayer(
    videoId: String,
    sectionDurationSeconds: Int? = null,
    onSectionComplete: () -> Unit = {},
    onPlayNextSection: () -> Unit = {},
    onWebViewCreated: (WebView) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Create a JavaScript interface object that can be referenced by the WebView
    val jsInterface = remember {
        object {
            @android.webkit.JavascriptInterface
            fun onSectionComplete() {
                onSectionComplete()
            }
            
            @android.webkit.JavascriptInterface
            fun playNextSection() {
                onPlayNextSection()
            }
        }
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mediaPlaybackRequiresUserGesture = false
                }
                webViewClient = WebViewClient()
                
                // Add console logging
                setWebChromeClient(object : android.webkit.WebChromeClient() {
                    override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                        android.util.Log.d("YouTubePlayer", "Console: ${message.message()}")
                        return true
                    }
                })
                
                // Add JavaScript interface
                addJavascriptInterface(jsInterface, "Android")
                
                // Notify parent that WebView is created
                onWebViewCreated(this)
            }
        },
        update = { webView ->
            val embedUrl = "https://www.youtube.com/embed/$videoId?autoplay=0&rel=0&enablejsapi=1&origin=${android.net.Uri.parse("https://www.youtube.com")}&controls=1&modestbranding=1&showinfo=0&iv_load_policy=3&cc_load_policy=0&fs=1&disablekb=0&loop=0&playlist=$videoId"
            
            // Debug logging for section duration
            android.util.Log.d("YouTubePlayer", "ironman: sectionDurationSeconds = $sectionDurationSeconds")
            android.util.Log.d("YouTubePlayer", "ironman: sectionDurationSeconds != null = ${sectionDurationSeconds != null}")
            android.util.Log.d("YouTubePlayer", "ironman: sectionDurationSeconds > 0 = ${sectionDurationSeconds != null && sectionDurationSeconds > 0}")
            
            val sectionControlScript = if (sectionDurationSeconds != null && sectionDurationSeconds > 0) {
                android.util.Log.d("YouTubePlayer", "ironman: Loading SECTION MODE JavaScript code")
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <style>
                        body { margin: 0; padding: 0; }
                        #player { width: 100%; height: 100%; }
                    </style>
                </head>
                <body>
                    <div id="player"></div>
                    
                    <script src="https://www.youtube.com/iframe_api"></script>
                    <script>
                        console.log('ironman: Section mode JavaScript code loaded');
                        // Section mode variables
                        var sectionDuration = $sectionDurationSeconds;
                        var sectionStartTime = 0;
                        var currentSection = 0;
                        var totalSections = 0;
                        var isSectionMode = false;
                        var isPaused = false;
                        var playerReady = false;
                        var apiReady = false;
                        var player = null;
                        var checkInterval = null;
                        var countdownTimer = null;
                        var sectionTimer = null;
                        var countdownStartTime = 0;
                        var readyCheckInterval = null;
                        
                        console.log('ironman: Variables initialized - sectionDuration:', sectionDuration);
                        
                        // Global function that YouTube API calls when ready
                        function onYouTubeIframeAPIReady() {
                            console.log('ironman: YouTube API Ready for video: $videoId');
                            apiReady = true;
                            createPlayer();
                        }
                        
                        function createPlayer() {
                            console.log('ironman: Creating player with section mode enabled');
                            console.log('ironman: About to create YT.Player instance');
                            
                            // Check if the player div exists
                            var playerDiv = document.getElementById('player');
                            console.log('ironman: player div found:', !!playerDiv);
                            
                            if (!playerDiv) {
                                console.log('ironman: ERROR - player div not found, creating it');
                                playerDiv = document.createElement('div');
                                playerDiv.id = 'player';
                                document.body.appendChild(playerDiv);
                            }
                            
                            player = new YT.Player('player', {
                                height: '100%',
                                width: '100%',
                                videoId: '$videoId',
                                playerVars: {
                                    'autoplay': 0,
                                    'rel': 0,
                                    'enablejsapi': 1,
                                    'origin': window.location.origin || 'https://www.youtube.com',
                                    'controls': 1,
                                    'modestbranding': 1,
                                    'showinfo': 0,
                                    'iv_load_policy': 3,
                                    'cc_load_policy': 0,
                                    'fs': 1,
                                    'disablekb': 0
                                },
                                events: {
                                    'onReady': function(event) {
                                        console.log('ironman: onReady event triggered');
                                        onPlayerReady(event);
                                    },
                                    'onStateChange': function(event) {
                                        console.log('ironman: onStateChange event triggered, state:', event.data);
                                        onPlayerStateChange(event);
                                    },
                                    'onError': function(event) {
                                        console.log('ironman: Player error:', event.data);
                                    }
                                }
                            });
                            console.log('ironman: YT.Player instance created, player object:', !!player);
                            
                            // Add a periodic check to see if player becomes ready
                            var readyCheckInterval = setInterval(function() {
                                if (player) {
                                    // Check if the player has the required methods
                                    if (typeof player.getPlayerState === 'function') {
                                        try {
                                            var state = player.getPlayerState();
                                            console.log('ironman: Player state check - state:', state);
                                            if (state !== -1) { // -1 is unstarted
                                                console.log('ironman: Player is no longer unstarted, clearing ready check');
                                                clearInterval(readyCheckInterval);
                                                
                                                // If onReady hasn't been called yet, call it manually
                                                if (!playerReady) {
                                                    console.log('ironman: onReady event never fired, calling manually');
                                                    onPlayerReady({data: state});
                                                }
                                            }
                                        } catch (error) {
                                            console.log('ironman: Error checking player state:', error);
                                        }
                                    } else {
                                        console.log('ironman: Player exists but getPlayerState method not available yet');
                                    }
                                } else {
                                    console.log('ironman: Player not ready yet for state check');
                                }
                            }, 1000);
                            
                            // Add a timeout to force ready state if onReady doesn't fire
                            setTimeout(function() {
                                if (!playerReady && player) {
                                    console.log('ironman: onReady timeout reached, checking if we can force ready state');
                                    try {
                                        // Check if player methods are available
                                        if (typeof player.getPlayerState === 'function') {
                                            var state = player.getPlayerState();
                                            console.log('ironman: Forcing onReady with state:', state);
                                            onPlayerReady({data: state});
                                        } else {
                                            console.log('ironman: Cannot force ready state - getPlayerState not available');
                                            // Try to force ready state anyway
                                            console.log('ironman: Forcing onReady without state check');
                                            onPlayerReady({data: 0}); // Assume ready state
                                        }
                                    } catch (error) {
                                        console.log('ironman: Error forcing ready state:', error);
                                        // Try to force ready state anyway
                                        console.log('ironman: Forcing onReady after error');
                                        onPlayerReady({data: 0}); // Assume ready state
                                    }
                                }
                            }, 5000); // 5 second timeout
                        }
                        
                        // Fallback: if API doesn't load within 10 seconds, try to create player anyway
                        setTimeout(function() {
                            if (!apiReady) {
                                console.log('ironman: YouTube API not ready after 10 seconds, trying to create player anyway');
                                createPlayer();
                            }
                        }, 10000);
                        
                        function onPlayerReady(event) {
                            console.log('ironman: Player ready for video: $videoId');
                            console.log('ironman: Section duration set to:', sectionDuration, 'seconds');
                            playerReady = true;
                            sectionStartTime = 0;
                            currentSection = 0;
                            
                            // Calculate total sections based on video duration
                            if (player && typeof player.getDuration === 'function') {
                                try {
                                    var videoDuration = player.getDuration();
                                    totalSections = Math.ceil(videoDuration / sectionDuration);
                                    console.log('ironman: Video duration:', videoDuration, 'seconds, Total sections:', totalSections);
                                } catch (e) {
                                    console.log('ironman: Error getting video duration, using default:', e);
                                    totalSections = 10; // Default fallback
                                }
                            } else {
                                console.log('ironman: Cannot get video duration, using default total sections');
                                totalSections = 10; // Default fallback
                            }
                            
                            isSectionMode = true;
                            isPaused = false;
                            console.log('ironman: Section mode initialized - start time:', sectionStartTime, 'end time:', sectionStartTime + sectionDuration);
                            
                            // Start countdown timer automatically as fallback
                            setTimeout(function() {
                                if (typeof window.startCountdownTimer === 'function') {
                                    console.log('ironman: Auto-starting countdown timer as fallback');
                                    window.startCountdownTimer();
                                }
                            }, 2000); // Wait 2 seconds after ready
                        }
                        
                        function onPlayerStateChange(event) {
                            console.log('ironman: Player state changed:', event.data, 'for video: $videoId');
                            
                            // YouTube Player States:
                            // -1 (unstarted), 0 (ended), 1 (playing), 2 (paused), 3 (buffering), 5 (video cued)
                            
                            if (event.data == YT.PlayerState.PLAYING && isSectionMode && playerReady) {
                                console.log('ironman: Video started playing, starting section timer');
                                isPaused = false;
                                startSectionTimer();
                                
                                // Also start countdown timer as backup
                                if (typeof window.startCountdownTimer === 'function') {
                                    console.log('ironman: Starting countdown timer as backup');
                                    window.startCountdownTimer();
                                }
                            } else if (event.data == YT.PlayerState.PAUSED) {
                                console.log('ironman: Video paused, stopping section timer');
                                isPaused = true;
                                stopSectionTimer();
                                
                                // Stop countdown timer
                                if (countdownTimer) {
                                    clearTimeout(countdownTimer);
                                    countdownTimer = null;
                                }
                            } else if (event.data == YT.PlayerState.ENDED) {
                                console.log('ironman: Video ended, stopping section timer');
                                stopSectionTimer();
                                
                                // Stop countdown timer
                                if (countdownTimer) {
                                    clearTimeout(countdownTimer);
                                    countdownTimer = null;
                                }
                            } else if (event.data == YT.PlayerState.BUFFERING) {
                                console.log('ironman: Video buffering, keeping timer running');
                            }
                        }
                        
                        function startSectionTimer() {
                            if (checkInterval) {
                                console.log('ironman: Timer already running, clearing existing timer');
                                clearInterval(checkInterval);
                            }
                            console.log('ironman: Starting section timer for', sectionDuration, 'seconds');
                            console.log('ironman: Section will end at:', sectionStartTime + sectionDuration, 'seconds');
                            checkInterval = setInterval(checkSectionTime, 500); // Check every 500ms
                        }
                        
                        function stopSectionTimer() {
                            if (checkInterval) {
                                console.log('ironman: Stopping section timer');
                                clearInterval(checkInterval);
                                checkInterval = null;
                            }
                        }
                        
                        function checkSectionTime() {
                            if (!player || !playerReady) {
                                console.log('ironman: Player not ready yet');
                                return;
                            }
                            
                            if (!player.getCurrentTime) {
                                console.log('ironman: getCurrentTime method not available');
                                return;
                            }
                            
                            try {
                                var currentTime = player.getCurrentTime();
                                var sectionEndTime = sectionStartTime + sectionDuration;
                                
                                console.log('ironman: Current time:', currentTime, 'Section start:', sectionStartTime, 'Section end:', sectionEndTime, 'Duration:', sectionDuration);
                                
                                if (currentTime >= sectionEndTime) {
                                    console.log('ironman: Section complete, pausing video at', currentTime, 'seconds');
                                    stopSectionTimer();
                                    isSectionMode = false;
                                    isPaused = true;
                                    
                                    // Use YouTube API to pause video
                                    if (player && typeof player.pauseVideo === 'function') {
                                        try {
                                            player.pauseVideo();
                                            console.log('ironman: Video paused successfully using YouTube API');
                                            Android.onSectionComplete();
                                        } catch (error) {
                                            console.log('ironman: Error pausing video:', error);
                                            // Fallback to other methods if pauseVideo fails
                                            tryAlternativePauseMethods();
                                        }
                                    } else {
                                        console.log('ironman: pauseVideo method not available, trying alternative methods');
                                        tryAlternativePauseMethods();
                                    }
                                }
                            } catch (error) {
                                console.error('ironman: Error checking section time:', error);
                                // If there's an error, stop the timer to prevent further issues
                                stopSectionTimer();
                            }
                        }
                        
                        function tryAlternativePauseMethods() {
                            // Try stopVideo
                            if (player && typeof player.stopVideo === 'function') {
                                try {
                                    player.stopVideo();
                                    console.log('ironman: Video stopped successfully using stopVideo');
                                    Android.onSectionComplete();
                                    return;
                                } catch (error) {
                                    console.log('ironman: Error stopping video:', error);
                                }
                            }
                            
                            // Try seekTo(0)
                            if (player && typeof player.seekTo === 'function') {
                                try {
                                    player.seekTo(0, true);
                                    console.log('ironman: Video seeked to beginning successfully');
                                    Android.onSectionComplete();
                                    return;
                                } catch (error) {
                                    console.log('ironman: Error seeking video:', error);
                                }
                            }
                            
                            // If all YouTube API methods fail, notify Android anyway
                            console.log('ironman: All YouTube API methods failed, but section is complete');
                            Android.onSectionComplete();
                        }
                        
                        // Simple countdown timer (fallback when YouTube methods aren't available)
                        window.startCountdownTimer = function() {
                            console.log('ironman: Starting countdown timer for', sectionDuration, 'seconds');
                            if (countdownTimer) {
                                clearTimeout(countdownTimer);
                            }
                            
                            countdownStartTime = Date.now();
                            var remainingTime = sectionDuration;
                            
                            function updateCountdown() {
                                var elapsed = Math.floor((Date.now() - countdownStartTime) / 1000);
                                remainingTime = Math.max(0, sectionDuration - elapsed);
                                
                                console.log('ironman: Countdown - elapsed:', elapsed, 'remaining:', remainingTime);
                                
                                if (remainingTime <= 0) {
                                    console.log('ironman: Countdown complete, attempting to pause video');
                                    clearTimeout(countdownTimer);
                                    
                                    // Try YouTube API methods first
                                    if (player && typeof player.pauseVideo === 'function') {
                                        try {
                                            console.log('ironman: Trying YouTube API pauseVideo from countdown');
                                            player.pauseVideo();
                                            console.log('ironman: SUCCESS - YouTube API pauseVideo worked from countdown');
                                            Android.onSectionComplete();
                                            return;
                                        } catch (e) {
                                            console.log('ironman: Countdown pauseVideo error:', e.message);
                                        }
                                    }
                                    
                                    // Try other methods
                                    if (player && typeof player.stopVideo === 'function') {
                                        try {
                                            console.log('ironman: Trying YouTube API stopVideo from countdown');
                                            player.stopVideo();
                                            console.log('ironman: SUCCESS - YouTube API stopVideo worked from countdown');
                                            Android.onSectionComplete();
                                            return;
                                        } catch (e) {
                                            console.log('ironman: Countdown stopVideo error:', e.message);
                                        }
                                    }
                                    
                                    // If all methods fail, notify Android anyway
                                    console.log('ironman: All countdown pause methods failed, but section is complete');
                                    Android.onSectionComplete();
                                } else {
                                    countdownTimer = setTimeout(updateCountdown, 1000);
                                }
                            }
                            
                            updateCountdown();
                        };
                        
                        // Enhanced pause function that uses YouTube API methods
                        window.forcePauseVideo = function() {
                            console.log('ironman: Force pause video called');
                            
                            // Method 1: Try YouTube API pauseVideo first
                            if (player && typeof player.pauseVideo === 'function') {
                                try {
                                    console.log('ironman: Trying YouTube API pauseVideo');
                                    player.pauseVideo();
                                    console.log('ironman: SUCCESS - YouTube API pauseVideo worked');
                                    Android.onSectionComplete();
                                    return;
                                } catch (e) {
                                    console.log('ironman: pauseVideo error:', e.message);
                                }
                            }
                            
                            // Method 2: Try YouTube API stopVideo
                            if (player && typeof player.stopVideo === 'function') {
                                try {
                                    console.log('ironman: Trying YouTube API stopVideo');
                                    player.stopVideo();
                                    console.log('ironman: SUCCESS - YouTube API stopVideo worked');
                                    Android.onSectionComplete();
                                    return;
                                } catch (e) {
                                    console.log('ironman: stopVideo error:', e.message);
                                }
                            }
                            
                            // Method 3: Try YouTube API seekTo(0)
                            if (player && typeof player.seekTo === 'function') {
                                try {
                                    console.log('ironman: Trying YouTube API seekTo(0)');
                                    player.seekTo(0, true);
                                    console.log('ironman: SUCCESS - YouTube API seekTo(0) worked');
                                    Android.onSectionComplete();
                                    return;
                                } catch (e) {
                                    console.log('ironman: seekTo error:', e.message);
                                }
                            }
                            
                            console.log('ironman: All YouTube API methods failed');
                        };
                        
                        // Function to go to next section
                        window.goToNextSection = function() {
                            console.log('ironman: Going to next section');
                            currentSection++;
                            if (currentSection >= totalSections) {
                                currentSection = 0; // Loop back to first section
                            }
                            sectionStartTime = currentSection * sectionDuration;
                            
                            // Stop any existing timers
                            if (sectionTimer) {
                                clearTimeout(sectionTimer);
                                sectionTimer = null;
                            }
                            if (countdownTimer) {
                                clearTimeout(countdownTimer);
                                countdownTimer = null;
                            }
                            
                            // Seek to the new section start time
                            if (player && typeof player.seekTo === 'function') {
                                try {
                                    player.seekTo(sectionStartTime, true);
                                    console.log('ironman: Successfully seeked to next section at', sectionStartTime, 'seconds');
                                    
                                    // Start playing the video
                                    setTimeout(function() {
                                        if (player && typeof player.playVideo === 'function') {
                                            player.playVideo();
                                            console.log('ironman: Started playing video at new section');
                                        }
                                    }, 500);
                                    
                                    // Notify Android about section change
                                    Android.onSectionChanged(currentSection, totalSections, sectionStartTime);
                                } catch (e) {
                                    console.log('ironman: Error seeking to next section:', e);
                                }
                            }
                        };
                        
                        // Function to go to previous section
                        window.goToPreviousSection = function() {
                            console.log('ironman: Going to previous section');
                            currentSection--;
                            if (currentSection < 0) {
                                currentSection = totalSections - 1; // Loop to last section
                            }
                            sectionStartTime = currentSection * sectionDuration;
                            
                            // Stop any existing timers
                            if (sectionTimer) {
                                clearTimeout(sectionTimer);
                                sectionTimer = null;
                            }
                            if (countdownTimer) {
                                clearTimeout(countdownTimer);
                                countdownTimer = null;
                            }
                            
                            // Seek to the new section start time
                            if (player && typeof player.seekTo === 'function') {
                                try {
                                    player.seekTo(sectionStartTime, true);
                                    console.log('ironman: Successfully seeked to previous section at', sectionStartTime, 'seconds');
                                    
                                    // Start playing the video
                                    setTimeout(function() {
                                        if (player && typeof player.playVideo === 'function') {
                                            player.playVideo();
                                            console.log('ironman: Started playing video at new section');
                                        }
                                    }, 500);
                                    
                                    // Notify Android about section change
                                    Android.onSectionChanged(currentSection, totalSections, sectionStartTime);
                                } catch (e) {
                                    console.log('ironman: Error seeking to previous section:', e);
                                }
                            }
                        };
                        
                        // Function to restart current section
                        window.restartCurrentSection = function() {
                            console.log('ironman: Restarting current section');
                            
                            // Stop any existing timers
                            if (sectionTimer) {
                                clearTimeout(sectionTimer);
                                sectionTimer = null;
                            }
                            if (countdownTimer) {
                                clearTimeout(countdownTimer);
                                countdownTimer = null;
                            }
                            
                            // Seek to the current section start time
                            if (player && typeof player.seekTo === 'function') {
                                try {
                                    player.seekTo(sectionStartTime, true);
                                    console.log('ironman: Successfully seeked to restart section at', sectionStartTime, 'seconds');
                                    
                                    // Start playing the video
                                    setTimeout(function() {
                                        if (player && typeof player.playVideo === 'function') {
                                            player.playVideo();
                                            console.log('ironman: Started playing video at restarted section');
                                        }
                                    }, 500);
                                    
                                    // Notify Android about section restart
                                    Android.onSectionRestarted(currentSection, totalSections, sectionStartTime);
                                } catch (e) {
                                    console.log('ironman: Error seeking to restart section:', e);
                                }
                            }
                        };
                    </script>
                </body>
                </html>
                """
            } else {
                android.util.Log.d("YouTubePlayer", "ironman: Loading NO SECTION MODE JavaScript code")
                """
                <script src="https://www.youtube.com/iframe_api"></script>
                <script>
                    console.log('ironman: No section mode JavaScript code loaded');
                    function onYouTubeIframeAPIReady() {
                        console.log('ironman: YouTube API Ready for video: $videoId (no section mode)');
                    }
                </script>
                """
            }
            
            val html =
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { 
                            margin: 0; 
                            padding: 0; 
                            background: #000;
                        }
                        iframe { 
                            width: 100%; 
                            height: 100%; 
                            border: none; 
                        }
                    </style>
                    $sectionControlScript
                </head>
                <body>
                    <iframe id="player" src="$embedUrl"
                            allowfullscreen
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture">
                    </iframe>
                </body>
                </html>
                """.trimIndent()
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        },
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
    )
}
