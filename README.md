# YouTube for Students - Learning Focused Video Player

A specialized YouTube player Android app designed for students and learners who want to consume complex topics and long educational videos more effectively.

## Purpose

Traditional YouTube players can be overwhelming when learning complex subjects. This app helps you break down long videos into manageable, digestible sections, making it easier to:
- **Rewind and replay** specific parts without losing your place
- **Focus on one concept** at a time
- **Avoid information overload** from long videos
- **Build better learning habits** through structured video consumption

## Key Features

### 🎯 Sectioned Video Playback
- **Configurable Duration**: Set how long each video section should be (e.g., 2, 5, 10, or 15 minutes)

### 📚 Learning-Focused Design
- **Minimal Distractions**: Clean interface focused on content consumption

### 🎨 User Experience
- **Responsive Design**: Works seamlessly on phones and tablets
- **Material Design 3**: Modern, accessible interface
- **Offline Capability**: Works with downloaded content (future feature)

## How to Use

### 1. Configure Section Duration
- Open the app settings
- Choose your preferred section duration (2, 5, 10, or 15 minutes)
- This determines how long each video segment will play

### 2. Play Videos in Sections
1. **Select a Video**: Choose from pre-loaded educational content or add your own
2. **Start Sectioned Playback**: Tap the "Play Section" button
3. **Watch Current Section**: The video plays for your configured duration
4. **Advance to Next Section**: Tap "Next Section" to continue
5. **Repeat**: Continue until you've watched the entire video

### 3. Add Custom Educational Videos
1. Find a YouTube video ID (the part after `v=` in YouTube URLs)
2. Paste it in the "YouTube Video ID" field
3. Click "Add Video" to include it in your learning library

## Getting YouTube Video IDs

To get a YouTube video ID:
1. Go to any YouTube video
2. Copy the URL (e.g., `https://www.youtube.com/watch?v=dQw4w9WgXcQ`)
3. The video ID is the part after `v=` (in this case: `dQw4w9WgXcQ`)

## Learning Benefits

- **Better Retention**: Shorter sections help you absorb information more effectively
- **Reduced Cognitive Load**: Focus on one concept at a time
- **Active Learning**: Manual advancement encourages engagement
- **Flexible Pacing**: Set your own learning rhythm
- **Review-Friendly**: Easy to replay sections you need to review

## Technical Details

- Built with **Jetpack Compose** for modern, responsive UI
- Uses **WebView** to embed YouTube's iframe player
- **Material Design 3** for consistent, accessible theming
- **Kotlin** for clean, maintainable Android development
- **Sectioned Playback Engine** for controlled video consumption

## Requirements

- Android API level 24+ (Android 7.0+)
- Internet connection for video playback
- YouTube video content

## Building the App

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device or emulator

## Kotlin Code Quality

This project uses **ktlint** to maintain consistent Kotlin code style and quality.

### Prerequisites
- Java 17 (required for Android Gradle Plugin)
- Gradle 8.13+

### Available Commands

#### Using Gradle directly:
```bash
# Check code style without making changes
./gradlew ktlintCheck

# Format code to match style rules
./gradlew ktlintFormat
```

#### Using the convenience script:
```bash
# Check code style without making changes
./lint.sh check

# Format code to match style rules
./lint.sh format

# Check and format if needed
./lint.sh apply
```

### Configuration

The project includes:
- **`.editorconfig`**: Defines code style rules for ktlint
- **`app/build.gradle.kts`**: ktlint plugin configuration
- **`lint.sh`**: Convenience script for running ktlint commands

### Code Style Rules

The project follows the official Kotlin coding style guide with some customizations:
- 4-space indentation
- 120 character line length
- Official Kotlin code style (`ktlint_official`)
- Import ordering and formatting
- Consistent spacing and formatting

### IDE Integration

For the best development experience:
1. Install the ktlint plugin in your IDE
2. Enable "Format on Save" with ktlint
3. Use the provided `.editorconfig` file for consistent formatting

## Permissions

The app requires:
- `INTERNET`: To load YouTube videos
- `ACCESS_NETWORK_STATE`: To check network connectivity

## Sample Educational Content

The app includes sample videos covering various subjects:
- Science and Technology
- Mathematics and Engineering
- Language Learning
- History and Philosophy
- Programming and Computer Science

## Future Features

- **Bookmarking**: Save important sections for later review
- **Notes Integration**: Add personal notes to video sections
- **Playlist Support**: Create learning playlists
- **Offline Downloads**: Download videos for offline learning
- **Progress Sync**: Sync learning progress across devices
- **Learning Analytics**: Track your learning patterns and progress

## Contributing

This app is designed for students and learners. If you have ideas for features that would help with learning complex topics, please contribute!

**Before contributing, please ensure your code passes ktlint checks:**
```bash
./lint.sh apply
```

---

**Transform your YouTube learning experience with structured, manageable video sections. Perfect for students, professionals, and anyone who wants to learn more effectively from long-form content.** 