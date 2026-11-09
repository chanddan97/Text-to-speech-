package com.example.data.model

enum class VoiceActor(
    val actorName: String,
    val title: String,
    val gender: String,
    val pitchMultiplier: Float,
    val speedMultiplier: Float,
    val description: String,
    val promptPersona: String
) {
    CHARON(
        actorName = "Charon",
        title = "Deep Narrator",
        gender = "Male",
        pitchMultiplier = 0.75f,
        speedMultiplier = 0.90f,
        description = "Deep cinematic baritone voice for trailers & dramatic narratives",
        promptPersona = "Speak as Charon: a deep, authoritative cinematic male voice with resonance and steady weight."
    ),
    FENRIR(
        actorName = "Fenrir",
        title = "Aggressive & Fiery",
        gender = "Male",
        pitchMultiplier = 0.92f,
        speedMultiplier = 1.15f,
        description = "Intense, rasping, passionate voice for confrontation and fury",
        promptPersona = "Speak as Fenrir: an intense, fierce male voice with raw energy and sharp dramatic punch."
    ),
    PUCK(
        actorName = "Puck",
        title = "Youthful & Vibrant",
        gender = "Male",
        pitchMultiplier = 1.15f,
        speedMultiplier = 1.05f,
        description = "Clear, modern, expressive young voice for dialogues and thoughts",
        promptPersona = "Speak as Puck: a vibrant, youthful male voice full of natural inflection and sincerity."
    ),
    ORUS(
        actorName = "Orus",
        title = "Resonant Elder",
        gender = "Male",
        pitchMultiplier = 0.68f,
        speedMultiplier = 0.85f,
        description = "Mature gravelly elder voice with solemn cinematic gravity",
        promptPersona = "Speak as Orus: a venerable, wise elder voice steeped in memory, gravitas, and solemn stillness."
    ),
    KORE(
        actorName = "Kore",
        title = "Gentle & Emotive",
        gender = "Female",
        pitchMultiplier = 1.25f,
        speedMultiplier = 0.92f,
        description = "Warm, delicate, empathetic female voice for heartfelt emotion",
        promptPersona = "Speak as Kore: a warm, deeply compassionate female voice carrying soft sorrow and tender empathy."
    ),
    AOEDE(
        actorName = "Aoede",
        title = "Lyrical & Tragic",
        gender = "Female",
        pitchMultiplier = 1.35f,
        speedMultiplier = 0.88f,
        description = "Poignant, haunting female voice with poetic theatrical cadence",
        promptPersona = "Speak as Aoede: a lyrical, tragic female voice filled with heartbreak, beauty, and emotional yearning."
    );

    companion object {
        fun fromName(name: String): VoiceActor {
            return entries.firstOrNull { it.actorName.equals(name, ignoreCase = true) } ?: CHARON
        }
    }
}
