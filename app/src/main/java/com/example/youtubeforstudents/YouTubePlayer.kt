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
            val embedUrl = "https://www.youtube.com/embed/$videoId?autoplay=0&rel=0&enablejsapi=1"
            
            val sectionControlScript = if (sectionDurationSeconds != null && sectionDurationSeconds > 0) {
                """
                <script>
                    var player;
                    var sectionDuration = $sectionDurationSeconds;
                    var sectionStartTime = 0;
                    var isSectionMode = true;
                    var isPaused = false;
                    var checkInterval;
                    
                    // Load YouTube IFrame API
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
                    
                    function onYouTubeIframeAPIReady() {
                        console.log('YouTube API Ready');
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$videoId',
                            playerVars: {
                                'autoplay': 0,
                                'rel': 0,
                                'enablejsapi': 1
                            },
                            events: {
                                'onReady': onPlayerReady,
                                'onStateChange': onPlayerStateChange
                            }
                        });
                    }
                    
                    function onPlayerReady(event) {
                        console.log('Player ready');
                        sectionStartTime = 0;
                        isSectionMode = true;
                    }
                    
                    function onPlayerStateChange(event) {
                        console.log('Player state changed:', event.data);
                        if (event.data == YT.PlayerState.PLAYING && isSectionMode) {
                            isPaused = false;
                            startSectionTimer();
                        } else if (event.data == YT.PlayerState.PAUSED) {
                            isPaused = true;
                            stopSectionTimer();
                        } else if (event.data == YT.PlayerState.ENDED) {
                            stopSectionTimer();
                        }
                    }
                    
                    function startSectionTimer() {
                        console.log('Starting section timer for', sectionDuration, 'seconds');
                        checkInterval = setInterval(checkSectionTime, 1000);
                    }
                    
                    function stopSectionTimer() {
                        if (checkInterval) {
                            clearInterval(checkInterval);
                            checkInterval = null;
                        }
                    }
                    
                    function checkSectionTime() {
                        if (player && player.getCurrentTime && !isPaused) {
                            var currentTime = player.getCurrentTime();
                            var sectionEndTime = sectionStartTime + sectionDuration;
                            
                            console.log('Current time:', currentTime, 'Section end:', sectionEndTime);
                            
                            if (currentTime >= sectionEndTime) {
                                console.log('Section complete, pausing video');
                                player.pauseVideo();
                                isSectionMode = false;
                                isPaused = true;
                                stopSectionTimer();
                                // Notify Android that section is complete
                                Android.onSectionComplete();
                            }
                        }
                    }
                    
                    function playNextSection() {
                        console.log('Playing next section');
                        if (player) {
                            sectionStartTime = player.getCurrentTime();
                            isSectionMode = true;
                            isPaused = false;
                            player.playVideo();
                        }
                    }
                    
                    function resetSectionMode() {
                        console.log('Resetting section mode');
                        if (player) {
                            sectionStartTime = 0;
                            isSectionMode = true;
                            isPaused = false;
                            player.seekTo(0);
                            player.playVideo();
                        }
                    }
                    
                    // Expose functions to Android
                    window.playNextSection = playNextSection;
                    window.resetSectionMode = resetSectionMode;
                </script>
                """
            } else ""
            
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
                    <div id="player"></div>
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
