

# Novel Weaver AI 📚🎬

**Novel Weaver AI** is an intelligent Android application designed to transform classic literature and custom user stories into interactive, cinematic audio-visual experiences powered by AI.

---

## ✨ Features & Application Overview

### 1. Splash Screen & Immersion
The app greets users with a sleek dark aesthetic and modern typography, introducing the core mission: weaving classic tales into live cinema.

<img src="./New%20folder/Screenshot_20260623_121506_com_aistudio_novelweaver_xtfzkp_MainActivity.jpg" width="300" alt="Splash Screen" />

### 2. Cinematic Shelf (Main Dashboard)
Organize and explore your digital library with ease.
* **Library Management**: Filter between all novels or your saved favorites and bookmarks.
* **Novel Cards**: View novel titles, author names, cover imagery, and status tags (e.g., `ANALYZED`).
* **Customization & Accessibility**: Switch seamlessly between English and Arabic (`العربية`) localizations, or toggle dark/light theme preferences.

<img src="./New%20folder/Screenshot_20260623_121508_com_aistudio_novelweaver_xtfzkp_MainActivity.jpg" width="300" alt="Cinematic Shelf" />

### 3. Import & AI Analysis Pipeline
Bring new stories to life with flexible import mechanisms.
* **PDF Auto-Extraction**: Select any PDF novel file to automatically extract story text.
* **Manual Input**: Enter custom title, author details, and paste raw story text or dialogue chapters.
* **AI Analysis Execution**: Trigger the AI pipeline with a single tap (*"Weave AI Pipeline & Analyze"*).

<img src="./New%20folder/Screenshot_20260623_121513_com_aistudio_novelweaver_xtfzkp_MainActivity.jpg" width="300" alt="Import Novel" />

### 4. Story Dashboard & Chronology Timeline
Immerse yourself in granular scene-by-scene breakdowns of your stories.
* **Chronology Timeline**: Walk through story scenes with emotional tone tagging (e.g., `MYSTERIOUS`, `TENSE`) and exact location details (e.g., *221B Baker Street*).
* **Interactive Views**: Switch between **Live Cinematic**, **Character**, and **Storyboard** perspectives.
* **Scene Playback**: Trigger playback for individual scenes directly from the timeline.

<img src="./New%20folder/Screenshot_20260623_121522_com_aistudio_novelweaver_xtfzkp_MainActivity.jpg" width="300" alt="Story Dashboard" />

### 5. Live Cinematic Experience
Experience stories brought to life with dynamic full-screen audio-visual playback.
* **Synchronized Narration**: Follow along with AI-generated narration and dialogue subtitles overlaid on scene visuals.
* **Full-Screen Media Player**: Interactive playback controls including play/pause, scene skip, and location context headers (*221B Baker Street - Living Room*).
* **AI Visual Generation**: Real-time imagery rendering matched to the current scene's mood and narrative context.

<img src="./New%20folder/Screenshot_20260623_121525_com_aistudio_novelweaver_xtfzkp_MainActivity.jpg" width="300" alt="Live Cinematic Experience" />

---

## 🚀 Run Locally

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device

