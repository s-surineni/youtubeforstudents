#!/bin/bash

# Set JAVA_HOME to Android Studio's embedded JDK
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Build the project
./gradlew assembleDebug

echo "Build completed!" 