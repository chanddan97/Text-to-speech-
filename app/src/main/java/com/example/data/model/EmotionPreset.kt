package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmotionAngry
import com.example.ui.theme.EmotionDeepStory
import com.example.ui.theme.EmotionHeartbroken
import com.example.ui.theme.EmotionMother
import com.example.ui.theme.EmotionWhisper

enum class EmotionPreset(
    val presetId: String,
    val displayName: String,
    val bengaliLabel: String,
    val promptInstruction: String,
    val pitchShift: Float,
    val rateShift: Float,
    val badgeColor: Color
) {
    ANGRY_SHOUT(
        presetId = "angry_shout",
        displayName = "Angry Shout",
        bengaliLabel = "তীব্র ক্রোধ ও চিৎকার",
        promptInstruction = "Deliver with explosive anger, ragged breathing, sudden sharp consonants, and fierce vocal strain.",
        pitchShift = 1.12f,
        rateShift = 1.22f,
        badgeColor = EmotionAngry
    ),
    DEEP_STORY(
        presetId = "deep_story",
        displayName = "Deep Story",
        bengaliLabel = "গভীর সিনেমাটিক আখ্যান",
        promptInstruction = "Deliver with steady, profound narrative stillness, deliberate slow cadence, and warm cinematic weight.",
        pitchShift = 0.90f,
        rateShift = 0.92f,
        badgeColor = EmotionDeepStory
    ),
    HEARTBROKEN_CRY(
        presetId = "heartbroken_cry",
        displayName = "Heartbroken Cry",
        bengaliLabel = "হৃদয়ভাঙা কান্না ও আর্তি",
        promptInstruction = "Deliver with tear-choked voice, subtle tremors, desperate breath catches, and heavy melancholy pauses.",
        pitchShift = 1.05f,
        rateShift = 0.85f,
        badgeColor = EmotionHeartbroken
    ),
    HOSPITAL_WHISPER(
        presetId = "hospital_whisper",
        displayName = "Hospital Whisper",
        bengaliLabel = "ফিসফিস ও নিস্তব্ধ উৎকণ্ঠা",
        promptInstruction = "Deliver in an intimate, breathless near-whisper, fragile pauses, tension, and quiet intimacy.",
        pitchShift = 0.95f,
        rateShift = 0.82f,
        badgeColor = EmotionWhisper
    ),
    MOTHER_EMOTIONAL(
        presetId = "mother_emotional",
        displayName = "Mother Emotional",
        bengaliLabel = "স্নেহময়ী মাতৃআবেগ",
        promptInstruction = "Deliver with overflowing tenderness, loving maternal tremor, comforting lilt, and tearful affection.",
        pitchShift = 1.10f,
        rateShift = 0.88f,
        badgeColor = EmotionMother
    );

    companion object {
        fun fromId(id: String): EmotionPreset {
            return entries.firstOrNull { it.presetId.equals(id, ignoreCase = true) } ?: DEEP_STORY
        }
    }
}
