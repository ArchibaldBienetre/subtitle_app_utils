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

Feature complete, with complete CLI on top.

## Useful commands, using the CLI

Run the gradle-based tests: 
```
./gradlew clean jvmTest --info
```

Build a jar of the current code:
```
./gradlew jvmJar
```

Run the jar we just built (e.g. for the version `1.0.2-SNAPSHOT`), streaming the subtitles from a file from the start, e.g. using `test_subtitles_start_00-00-00.srt` as input:
```
java -jar build/libs/subtitle_app_utils-jvm-1.0.2-SNAPSHOT.jar -i "$PWD/src/jvmTest/resources/test_subtitles_start_00-00-00.srt"
```

Run starting at the 5 second timestamp:
```
java -jar build/libs/subtitle_app_utils-jvm-1.0.2-SNAPSHOT.jar stream -i "$PWD/src/jvmTest/resources/test_subtitles_start_00-00-00.srt" -t 00:00:05
```