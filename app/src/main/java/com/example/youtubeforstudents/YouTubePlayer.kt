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
                <script src="https://www.youtube.com/iframe_api"></script>
                <script>
                    console.log('ironman: Section mode JavaScript code loaded');
                    var player;
                    var sectionDuration = $sectionDurationSeconds;
                    var sectionStartTime = 0;
                    var isSectionMode = true;
                    var isPaused = false;
                    var checkInterval;
                    var playerReady = false;
                    var countdownTimer = null;
                    var countdownStartTime = null;
                    
                    console.log('ironman: Variables initialized - sectionDuration:', sectionDuration);
                    
                    function onYouTubeIframeAPIReady() {
                        console.log('ironman: YouTube API Ready for video: $videoId');
                        console.log('ironman: Creating player with section mode enabled');
                        console.log('ironman: About to create YT.Player instance');
                        
                        // Check if the iframe element exists
                        var iframeElement = document.getElementById('player');
                        console.log('ironman: iframe element found:', !!iframeElement);
                        if (iframeElement) {
                            console.log('ironman: iframe src:', iframeElement.src);
                        }
                        
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$videoId',
                            playerVars: {
                                'autoplay': 0,
                                'rel': 0,
                                'enablejsapi': 1,
                                'origin': 'https://www.youtube.com'
                            },
                            events: {
                                'onReady': function(event) {
                                    console.log('ironman: onReady event triggered');
                                    onPlayerReady(event);
                                },
                                'onStateChange': function(event) {
                                    console.log('ironman: onStateChange event triggered, state:', event.data);
                                    onPlayerStateChange(event);
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
                    
                    function onPlayerReady(event) {
                        console.log('ironman: Player ready for video: $videoId');
                        console.log('ironman: Section duration set to:', sectionDuration, 'seconds');
                        playerReady = true;
                        sectionStartTime = 0;
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
                                
                                // Use a small delay to ensure the pause command is processed
                                setTimeout(function() {
                                    if (player && player.pauseVideo) {
                                        player.pauseVideo();
                                        console.log('ironman: Video paused successfully');
                                        // Notify Android that section is complete
                                        Android.onSectionComplete();
                                    }
                                }, 100);
                            }
                        } catch (error) {
                            console.error('ironman: Error checking section time:', error);
                            // If there's an error, stop the timer to prevent further issues
                            stopSectionTimer();
                        }
                    }
                    
                    function playNextSection() {
                        console.log('ironman: Playing next section');
                        if (player && playerReady) {
                            var currentTime = player.getCurrentTime();
                            sectionStartTime = currentTime;
                            isSectionMode = true;
                            isPaused = false;
                            console.log('ironman: Next section starting from:', sectionStartTime, 'will end at:', sectionStartTime + sectionDuration);
                            player.playVideo();
                        }
                    }
                    
                    function resetSectionMode() {
                        console.log('ironman: Resetting section mode');
                        if (player && playerReady) {
                            stopSectionTimer();
                            sectionStartTime = 0;
                            isSectionMode = true;
                            isPaused = false;
                            player.seekTo(0);
                            player.playVideo();
                        }
                    }
                    
                    function replaySection() {
                        console.log('ironman: Replaying current section from', sectionStartTime);
                        if (player && playerReady) {
                            stopSectionTimer();
                            isSectionMode = true;
                            isPaused = false;
                            player.seekTo(sectionStartTime);
                            player.playVideo();
                        }
                    }
                    
                    function debugSectionState() {
                        console.log('ironman: === Section Debug Info ===');
                        console.log('ironman: Player ready:', playerReady);
                        console.log('ironman: Player exists:', !!player);
                        console.log('ironman: Section mode:', isSectionMode);
                        console.log('ironman: Is paused:', isPaused);
                        console.log('ironman: Section duration:', sectionDuration);
                        console.log('ironman: Section start time:', sectionStartTime);
                        console.log('ironman: Timer running:', !!checkInterval);
                        
                        if (player && player.getCurrentTime) {
                            try {
                                var currentTime = player.getCurrentTime();
                                var sectionEndTime = sectionStartTime + sectionDuration;
                                console.log('ironman: Current time:', currentTime);
                                console.log('ironman: Section end time:', sectionEndTime);
                                console.log('ironman: Time remaining:', sectionEndTime - currentTime);
                            } catch (error) {
                                console.log('ironman: Error getting current time:', error);
                            }
                        }
                        
                        if (player && player.getPlayerState) {
                            try {
                                var state = player.getPlayerState();
                                console.log('ironman: Player state:', state);
                            } catch (error) {
                                console.log('ironman: Error getting player state:', error);
                            }
                        }
                        console.log('ironman: ========================');
                    }
                    
                    // Expose functions to Android
                    window.playNextSection = playNextSection;
                    window.resetSectionMode = resetSectionMode;
                    window.replaySection = replaySection;
                    window.debugSectionState = debugSectionState;
                    
                    // Manual trigger for section timer (fallback)
                    window.manualStartSectionTimer = function() {
                        console.log('ironman: Manual section timer start triggered');
                        if (player && isSectionMode) {
                            console.log('ironman: Manually starting section timer');
                            isPaused = false;
                            startSectionTimer();
                        } else {
                            console.log('ironman: Cannot start timer - player:', !!player, 'sectionMode:', isSectionMode);
                        }
                    };
                    
                    // Simple fallback - force ready state and start timer
                    window.forceReadyAndStart = function() {
                        console.log('ironman: Force ready and start triggered');
                        if (!playerReady) {
                            console.log('ironman: Forcing player ready state');
                            onPlayerReady({data: 0});
                        }
                        if (isSectionMode) {
                            console.log('ironman: Starting section timer after force ready');
                            isPaused = false;
                            startSectionTimer();
                        }
                    };
                    
                    // Simple start timer function
                    window.startSectionTimerNow = function() {
                        console.log('ironman: startSectionTimerNow called');
                        if (isSectionMode && playerReady) {
                            console.log('ironman: Starting section timer now');
                            isPaused = false;
                            startSectionTimer();
                        } else {
                            console.log('ironman: Cannot start timer - sectionMode:', isSectionMode, 'playerReady:', playerReady);
                        }
                    };
                    
                    // Wait for player methods to become available
                    function waitForPlayerMethods() {
                        console.log('ironman: Waiting for player methods to become available');
                        var methodCheckInterval = setInterval(function() {
                            if (player && typeof player.getCurrentTime === 'function' && typeof player.getPlayerState === 'function') {
                                console.log('ironman: Player methods are now available!');
                                clearInterval(methodCheckInterval);
                                
                                // Now we can properly initialize
                                if (!playerReady) {
                                    console.log('ironman: Player methods ready, calling onPlayerReady');
                                    onPlayerReady({data: 0});
                                }
                            } else {
                                console.log('ironman: Still waiting for player methods...');
                            }
                        }, 500);
                        
                        // Timeout after 10 seconds
                        setTimeout(function() {
                            clearInterval(methodCheckInterval);
                            console.log('ironman: Timeout waiting for player methods');
                        }, 10000);
                    }
                    
                    // Start waiting for methods when player is created
                    if (player) {
                        waitForPlayerMethods();
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
                            remainingTime = sectionDuration - elapsed;
                            
                            console.log('ironman: Countdown - elapsed:', elapsed, 'remaining:', remainingTime);
                            
                            if (remainingTime <= 0) {
                                console.log('ironman: Countdown complete, attempting to pause video');
                                
                                // Try the most aggressive method first - replace iframe with static image
                                try {
                                    var iframe = document.getElementById('player');
                                    if (iframe && iframe.parentNode) {
                                        var parent = iframe.parentNode;
                                        var img = document.createElement('img');
                                        img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODU0IiBoZWlnaHQ9IjQ4MCIgdmlld0JveD0iMCAwIDg1NCA0ODAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSI4NTQiIGhlaWdodD0iNDgwIiBmaWxsPSIjMDAwIi8+Cjx0ZXh0IHg9IjQyNyIgeT0iMjQwIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IndoaXRlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+U2VjdGlvbiBDb21wbGV0ZTwvdGV4dD4KPC9zdmc+';
                                        img.style.width = '100%';
                                        img.style.height = '100%';
                                        img.style.objectFit = 'cover';
                                        
                                        parent.removeChild(iframe);
                                        parent.appendChild(img);
                                        console.log('ironman: SUCCESS - Replaced iframe with static image');
                                        iframeReplaced = true; // Mark that iframe was replaced
                                        
                                        // Notify Android that section is complete
                                        Android.onSectionComplete();
                                        return;
                                    }
                                } catch (e) {
                                    console.log('ironman: Failed to replace iframe with image:', e.message);
                                }
                                
                                // Fallback: Try to remove iframe entirely
                                try {
                                    var iframe = document.getElementById('player');
                                    if (iframe && iframe.parentNode) {
                                        iframe.parentNode.removeChild(iframe);
                                        console.log('ironman: SUCCESS - Removed iframe entirely');
                                        
                                        // Notify Android that section is complete
                                        Android.onSectionComplete();
                                        return;
                                    }
                                } catch (e) {
                                    console.log('ironman: Failed to remove iframe:', e.message);
                                }
                                
                                // Fallback: Try to change iframe src to blank
                                try {
                                    var iframe = document.getElementById('player');
                                    if (iframe) {
                                        iframe.src = 'about:blank';
                                        console.log('ironman: SUCCESS - Changed iframe to blank page');
                                        
                                        // Notify Android that section is complete
                                        Android.onSectionComplete();
                                        return;
                                    }
                                } catch (e) {
                                    console.log('ironman: Failed to change iframe src:', e.message);
                                }
                                
                                // Final fallback: Try YouTube API methods
                                var pauseSuccess = false;
                                
                                // Method 1: Try YouTube API pauseVideo
                                if (player && typeof player.pauseVideo === 'function') {
                                    try {
                                        player.pauseVideo();
                                        console.log('ironman: Pause method 1 (pauseVideo) called');
                                        pauseSuccess = true;
                                    } catch (e) {
                                        console.log('ironman: Pause method 1 failed:', e.message);
                                    }
                                }
                                
                                // Method 2: Try to pause the iframe directly with postMessage
                                if (!pauseSuccess) {
                                    try {
                                        var iframe = document.getElementById('player');
                                        if (iframe && iframe.contentWindow) {
                                            iframe.contentWindow.postMessage('{"event":"command","func":"pauseVideo","args":""}', '*');
                                            console.log('ironman: Pause method 2 (postMessage) called');
                                            pauseSuccess = true;
                                        }
                                    } catch (e) {
                                        console.log('ironman: Pause method 2 failed:', e.message);
                                    }
                                }
                                
                                if (!pauseSuccess) {
                                    console.log('ironman: All pause methods failed, but section is complete');
                                }
                                
                                // Always notify Android that section is complete, even if pause failed
                                Android.onSectionComplete();
                                return;
                            }
                            
                            countdownTimer = setTimeout(updateCountdown, 1000);
                        }
                        
                        updateCountdown();
                    };
                    
                    // Enhanced pause function that tries multiple methods
                    window.forcePauseVideo = function() {
                        console.log('ironman: Force pause video called');
                        
                        // Try the most aggressive method first - replace iframe with static image
                        try {
                            var iframe = document.getElementById('player');
                            if (iframe && iframe.parentNode) {
                                var parent = iframe.parentNode;
                                var img = document.createElement('img');
                                img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODU0IiBoZWlnaHQ9IjQ4MCIgdmlld0JveD0iMCAwIDg1NCA0ODAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSI4NTQiIGhlaWdodD0iNDgwIiBmaWxsPSIjMDAwIi8+Cjx0ZXh0IHg9IjQyNyIgeT0iMjQwIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IndoaXRlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+U2VjdGlvbiBDb21wbGV0ZTwvdGV4dD4KPC9zdmc+';
                                img.style.width = '100%';
                                img.style.height = '100%';
                                img.style.objectFit = 'cover';
                                
                                parent.removeChild(iframe);
                                parent.appendChild(img);
                                console.log('ironman: Force pause - SUCCESS - Replaced iframe with static image');
                                return true;
                            }
                        } catch (e) {
                            console.log('ironman: Force pause - Failed to replace iframe with image:', e.message);
                        }
                        
                        // Fallback: Try to remove iframe entirely
                        try {
                            var iframe = document.getElementById('player');
                            if (iframe && iframe.parentNode) {
                                iframe.parentNode.removeChild(iframe);
                                console.log('ironman: Force pause - SUCCESS - Removed iframe entirely');
                                return true;
                            }
                        } catch (e) {
                            console.log('ironman: Force pause - Failed to remove iframe:', e.message);
                        }
                        
                        // Fallback: Try to change iframe src to blank
                        try {
                            var iframe = document.getElementById('player');
                            if (iframe) {
                                iframe.src = 'about:blank';
                                console.log('ironman: Force pause - SUCCESS - Changed iframe to blank page');
                                return true;
                            }
                        } catch (e) {
                            console.log('ironman: Force pause - Failed to change iframe src:', e.message);
                        }
                        
                        // Final fallback: Try YouTube API methods
                        var pauseSuccess = false;
                        
                        // Try all pause methods
                        if (player && typeof player.pauseVideo === 'function') {
                            try {
                                player.pauseVideo();
                                console.log('ironman: Force pause - method 1 (pauseVideo) called');
                                pauseSuccess = true;
                            } catch (e) {
                                console.log('ironman: Force pause - method 1 failed:', e.message);
                            }
                        }
                        
                        if (!pauseSuccess) {
                            try {
                                var iframe = document.getElementById('player');
                                if (iframe && iframe.contentWindow) {
                                    iframe.contentWindow.postMessage('{"event":"command","func":"pauseVideo","args":""}', '*');
                                    console.log('ironman: Force pause - method 2 (postMessage) called');
                                    pauseSuccess = true;
                                }
                            } catch (e) {
                                console.log('ironman: Force pause - method 2 failed:', e.message);
                            }
                        }
                        
                        if (!pauseSuccess && player && typeof player.stopVideo === 'function') {
                            try {
                                player.stopVideo();
                                console.log('ironman: Force pause - method 3 (stopVideo) called');
                                pauseSuccess = true;
                            } catch (e) {
                                console.log('ironman: Force pause - method 3 failed:', e.message);
                            }
                        }
                        
                        return pauseSuccess;
                    };
                    
                    // Section management variables
                    var currentSection = 0;
                    var totalSections = Math.ceil(player.getDuration() / sectionDuration);
                    var sectionStartTime = 0;
                    var originalIframeSrc = '';
                    var iframeReplaced = false;
                    
                    // Store original iframe source when player is ready
                    if (player && typeof player.getVideoUrl === 'function') {
                        try {
                            var iframe = document.getElementById('player');
                            if (iframe) {
                                originalIframeSrc = iframe.src;
                                console.log('ironman: Stored original iframe src:', originalIframeSrc);
                            }
                        } catch (e) {
                            console.log('ironman: Failed to store original iframe src:', e.message);
                        }
                    }
                    
                    // Function to restore iframe if it was replaced
                    window.restoreIframe = function() {
                        if (iframeReplaced) {
                            try {
                                var iframe = document.getElementById('player');
                                if (iframe && originalIframeSrc) {
                                    iframe.src = originalIframeSrc;
                                    iframeReplaced = false;
                                    console.log('ironman: Successfully restored iframe');
                                    return true;
                                }
                            } catch (e) {
                                console.log('ironman: Failed to restore iframe:', e.message);
                            }
                        }
                        return false;
                    };
                    
                    // Function to go to next section
                    window.goToNextSection = function() {
                        currentSection++;
                        if (currentSection >= totalSections) {
                            currentSection = 0; // Loop back to first section
                        }
                        sectionStartTime = currentSection * sectionDuration;
                        
                        console.log('ironman: Going to next section:', currentSection, 'start time:', sectionStartTime);
                        
                        // First, restore iframe if it was replaced
                        window.restoreIframe();
                        
                        // Seek to the start of the new section
                        if (player && typeof player.seekTo === 'function') {
                            try {
                                player.seekTo(sectionStartTime, true);
                                console.log('ironman: Successfully seeked to section start time');
                                
                                // Start the countdown timer for the new section
                                setTimeout(function() {
                                    window.startCountdownTimer();
                                }, 1000);
                                
                                // Notify Android about section change
                                Android.onSectionChanged(currentSection, totalSections, sectionStartTime);
                            } catch (e) {
                                console.log('ironman: Failed to seek to section:', e.message);
                            }
                        }
                    };
                    
                    // Function to go to previous section
                    window.goToPreviousSection = function() {
                        currentSection--;
                        if (currentSection < 0) {
                            currentSection = totalSections - 1; // Loop to last section
                        }
                        sectionStartTime = currentSection * sectionDuration;
                        
                        console.log('ironman: Going to previous section:', currentSection, 'start time:', sectionStartTime);
                        
                        // First, restore iframe if it was replaced
                        window.restoreIframe();
                        
                        // Seek to the start of the new section
                        if (player && typeof player.seekTo === 'function') {
                            try {
                                player.seekTo(sectionStartTime, true);
                                console.log('ironman: Successfully seeked to section start time');
                                
                                // Start the countdown timer for the new section
                                setTimeout(function() {
                                    window.startCountdownTimer();
                                }, 1000);
                                
                                // Notify Android about section change
                                Android.onSectionChanged(currentSection, totalSections, sectionStartTime);
                            } catch (e) {
                                console.log('ironman: Failed to seek to section:', e.message);
                            }
                        }
                    };
                    
                    // Function to restart current section
                    window.restartCurrentSection = function() {
                        sectionStartTime = currentSection * sectionDuration;
                        
                        console.log('ironman: Restarting current section:', currentSection, 'start time:', sectionStartTime);
                        
                        // First, restore iframe if it was replaced
                        window.restoreIframe();
                        
                        // Seek to the start of the current section
                        if (player && typeof player.seekTo === 'function') {
                            try {
                                player.seekTo(sectionStartTime, true);
                                console.log('ironman: Successfully seeked to section start time');
                                
                                // Start the countdown timer for the current section
                                setTimeout(function() {
                                    window.startCountdownTimer();
                                }, 1000);
                                
                                // Notify Android about section restart
                                Android.onSectionRestarted(currentSection, totalSections, sectionStartTime);
                            } catch (e) {
                                console.log('ironman: Failed to seek to section:', e.message);
                            }
                        }
                    };
                    
                    // Function to go to specific section
                    window.goToSection = function(sectionNumber) {
                        if (sectionNumber >= 0 && sectionNumber < totalSections) {
                            currentSection = sectionNumber;
                            sectionStartTime = currentSection * sectionDuration;
                            
                            console.log('ironman: Going to section:', currentSection, 'start time:', sectionStartTime);
                            
                            // First, restore iframe if it was replaced
                            window.restoreIframe();
                            
                            // Seek to the start of the specified section
                            if (player && typeof player.seekTo === 'function') {
                                try {
                                    player.seekTo(sectionStartTime, true);
                                    console.log('ironman: Successfully seeked to section start time');
                                    
                                    // Start the countdown timer for the new section
                                    setTimeout(function() {
                                        window.startCountdownTimer();
                                    }, 1000);
                                    
                                    // Notify Android about section change
                                    Android.onSectionChanged(currentSection, totalSections, sectionStartTime);
                                } catch (e) {
                                    console.log('ironman: Failed to seek to section:', e.message);
                                }
                            }
                        }
                    };
                    
                    // Function to get current section info
                    window.getCurrentSectionInfo = function() {
                        return {
                            currentSection: currentSection,
                            totalSections: totalSections,
                            sectionStartTime: sectionStartTime,
                            sectionDuration: sectionDuration
                        };
                    };
                </script>
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
