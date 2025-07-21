# YouTube for Students

An Android app that allows students to watch YouTube videos in manageable sections for focused learning sessions.

## Features

- **YouTube Video Player**: Embedded YouTube video player with full functionality
- **Section-Based Playback**: Play videos in customizable time sections
- **Manual Section Control**: User controls when to play the next section
- **Custom Video Input**: Enter any YouTube video ID to play custom content
- **Simple Duration Input**: Number field with radio buttons for seconds/minutes
- **Dedicated Settings Screen**: Clean separation of controls and content

## App Structure

### Main Screen
- **Video Player**: Large, prominent video player
- **Video Library**: Extensive list of educational videos
- **Search Functionality**: Find videos quickly
- **Custom Video Input**: Play any YouTube video by ID
- **Section Status**: Shows current section progress (when enabled)
- **Settings Button**: Access section mode configuration

### Settings Screen
- **Section Mode Configuration**: Enable/disable and configure section duration
- **Duration Controls**: Number input with seconds/minutes radio buttons
- **How It Works Guide**: Step-by-step instructions
- **Learning Tips**: Best practices for effective learning

## Section-Based Learning Feature

The app now includes a powerful section-based learning system that allows users to:

1. **Set Section Duration**: Specify how long each section should be using a number and time unit
2. **Controlled Playback**: Video automatically pauses after each section
3. **Manual Progression**: User must press "Play Next Section" to continue
4. **Section Tracking**: Shows current section number and status
5. **Restart Capability**: Restart the video from the beginning at any time

### How to Use Section Mode

1. Tap the **Settings** button (gear icon) in the top-right corner
2. Toggle "Enable Section Mode" to activate the feature
3. Enter a number in the "Duration" field
4. Select "Seconds" or "Minutes" using the radio buttons
5. Return to the main screen and play any video
6. Video automatically pauses after the specified duration
7. Press "Play Next Section" to continue to the next segment
8. Use the "Restart" button to start over from the beginning

### Input Controls

- **Number Field**: Enter any positive number (e.g., 30, 2, 5)
- **Radio Buttons**: Choose between "Seconds" or "Minutes"
- **Real-time Preview**: See the calculated duration as you type
- **Input Validation**: Only allows valid numbers

### Example Inputs

- **30 seconds**: Enter "30" and select "Seconds"
- **2 minutes**: Enter "2" and select "Minutes"
- **5 minutes**: Enter "5" and select "Minutes"
- **45 seconds**: Enter "45" and select "Seconds"

### Example Use Cases

- **Focused Learning**: Set sections to "2 minutes" for concentrated study periods
- **Review Sessions**: Use "30 seconds" sections for quick concept reviews
- **Long Videos**: Break down hour-long lectures into "10 minutes" sections
- **Active Learning**: Manual progression encourages engagement and reflection

## Building the Project

### Prerequisites

- Android Studio
- Android SDK
- Java Development Kit (JDK)

### Build Commands

```bash
# Using the provided build script (recommended)
./build.sh

# Or manually with correct JAVA_HOME
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

### Running the App

1. Build the project using the commands above
2. Install the APK on an Android device or emulator
3. The app will be available as "YouTube for Students"

## Technical Implementation

### Navigation Architecture

- **Single Activity**: MainActivity manages navigation between screens
- **State Management**: Shared state between main and settings screens
- **Composable Navigation**: Screen switching using Compose state
- **Back Navigation**: Settings screen includes back button

### YouTube Player Integration

The app uses a WebView-based YouTube player with the YouTube IFrame API for:
- Section-based playback control
- JavaScript-based time monitoring
- Automatic pause functionality
- Manual section progression

### Section Control Logic

- **Number Validation**: Ensures only positive numbers are accepted
- **Time Unit Conversion**: Converts seconds/minutes to total seconds
- **Real-time Monitoring**: JavaScript checks video progress every second
- **Automatic Pause**: Stops playback when section duration is reached
- **Section Tracking**: Maintains current section number and completion status
- **JavaScript Communication**: Android and WebView communicate via JavaScript interface

### UI Components

- **Main Screen**: Video player, library, search, and section status
- **Settings Screen**: Configuration controls, help, and tips
- **Section Toggle**: Switch to enable/disable section mode
- **Number Input Field**: Text field for section duration (numbers only)
- **Radio Buttons**: Seconds/Minutes selection
- **Section Status Card**: Shows current section and completion status
- **Control Buttons**: "Play Next Section" and "Restart" buttons
- **Section Counter**: Tracks current section number
- **Duration Preview**: Shows calculated section duration

## Sample Videos

The app includes several educational videos:
- Rick Astley - Never Gonna Give You Up
- PSY - GANGNAM STYLE
- Luis Fonsi - Despacito ft. Daddy Yankee
- Ylvis - The Fox (What Does The Fox Say?)

## Learning Benefits

- **Better Focus**: Shorter sections help maintain attention
- **Active Engagement**: Manual progression encourages thinking
- **Flexible Pacing**: Set your own learning rhythm
- **Reduced Overwhelm**: Break down long content into digestible pieces
- **Review-Friendly**: Easy to replay sections that need more attention
- **Simple Input**: Easy-to-use number and radio button interface
- **Clean Interface**: Separated concerns for better user experience

## Future Enhancements

- Save section duration presets for different learning scenarios
- Multiple video playlists with section controls
- Progress tracking across sessions
- Integration with learning management systems
- Offline section downloads
- Section notes and bookmarks
- Additional time units (hours)
- Video categories and filtering 