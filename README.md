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