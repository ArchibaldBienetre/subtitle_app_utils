# Subtitle App: utility library

CircleCI build status: [![CircleCI](https://circleci.com/gh/ArchibaldBienetre/subtitle_app_utils/tree/master.svg?style=svg)](https://circleci.com/gh/ArchibaldBienetre/subtitle_app_utils/tree/master)

## Main modules

### Scheduler 

Based on a list of entries that have a "from" timestamp and "to" timestamp associated with them, 
functionality for realtime-based streaming of data.

### Subtitles

Simple functions for subtitles (focused on SRT files): 
* (de)serialization, 
* reading / writing, 
* transforming (e.g. adjusting timestamp)
* timed streaming subtitles (using the scheduler functionality)

## Goal: usage in Android app

Goal of this library is being included as a git subtree or submodule in an Android app.  
That android app's purpose will be displaying SRT subtitles in real-time, alongside a film shown on another device.

## Status / "road map"

In the early stages, platform-independent development is not yet the main focus of development.

Instead, the basic modules will be implemented first, alongside with setting up infrastructure (code coverage, 
automated tests). 

## Useful commands, using the CLI

Run the gradle-based tests: 
```
./gradlew clean jvmTest --info
```

Build a jar of the current code:
```
./gradlew jvmJar
```

Run the jar we just built (e.g. for the version `0.9.2-SNAPSHOT`) from the start, using `SimpleCliTest_subtitles_fast.srt` as input:
```
java -jar build/libs/subtitle_app_utils-jvm-0.9.2-SNAPSHOT.jar -i "$PWD/src/jvmTest/resources/SimpleCliTest_subtitles_fast.srt"
```

Run the jar we just built, using `SimpleCliTest_subtitles_fast.srt` as input, starting at the 5 second timestamp:
```
java -jar build/libs/subtitle_app_utils-jvm-0.9.2-SNAPSHOT.jar -i "$PWD/src/jvmTest/resources/SimpleCliTest_subtitles_fast.srt" -t 00:00:05
```