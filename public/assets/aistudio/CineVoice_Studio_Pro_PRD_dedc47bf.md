# Product Requirements Document (PRD): CineVoice Studio Pro v2.5
**Objective:** Build a cinematic Bengali emotional voiceover & story-to-speech progressive web application powered entirely by Google Gemini API (`gemini-2.5-flash` for script direction & `gemini-2.5-flash-preview-tts` for multi-voice emotional audio synthesis).

---

## 1. Core Architecture & UI Requirements
- **Layout & Theme:** Mobile-first dark theme UI mimicking a professional native Android app (Tailwind or clean custom CSS). Bottom navigation bar with 4 tabs: `Studio`, `Audio FX`, `History`, and `Diagnostic`.
- **Secret API Vault:** No visible API key box on the main screen. Tapping the top app header 5 times rapidly triggers the secure modal prompt to input, test, and save the Google AI Studio API Key in `localStorage`.
- **Live Diagnostic & Test Modal:** Upon entering an API Key, execute a live step-by-step verification checklist displaying:
  1. Internet Connection (`✓ PASS`)
  2. Authentication (`✓ PASS`)
  3. Model Availability (`✓ PASS`)
  4. TTS Synthesis (`✓ PASS`)
  5. Audio Response & Latency (`✓ PASS` + Response Time in seconds).

---

## 2. Functional Modules & Features

### Module A: Auto Story Director (`gemini-2.5-flash`)
- Users paste raw Bengali stories/dialogues.
- The model parses and breaks the story into sequential scene blocks.
- Each scene block automatically assigns:
  - Text with micro-pauses (`...`, `[deep sigh]`).
  - Emotion Preset (`angry_shout`, `deep_story`, `heartbroken_cry`, `hospital_whisper`, `mother_emotional`).
  - Voice Actor (`Charon`, `Fenrir`, `Puck`, `Orus`, `Kore`, `Aoede`).
  - Silence Pause Duration slider (0.2s to 3.5s).

### Module B: Emotional TTS Generation (`gemini-2.5-flash-preview-tts`)
- Sends customized emotional system prompts alongside scene text to generate authentic human-like voice tokens (PCM 24kHz).
- Converts raw PCM to playable WAV audio blobs.
- Provides individual line regeneration and batch "Generate All" options.

### Module C: Cinematic Audio Post-Processing (Web Audio API)
- **Warm Bass Boost:** Shelf filter to add deep cinematic warmth to voices.
- **Subtle Room Reverb:** Convolver impulse response for emotional room acoustics.
- **BGM Auto-Ducking Mixer:** Integrates background piano audio (custom upload or built-in A-minor ambient pad). Automatically lowers BGM volume during speech and swells volume during emotional silence pauses.

### Module D: Master Export & Self-Diagnostic
- **Merge & Download:** Combines all scene audio chunks, pauses, FX, and ducked BGM into a single master WAV file.
- **Self-Diagnostic Scanner:** Built-in health check testing RAM heap usage, Web Audio engine state, and live API ping, with a one-click report copy button.