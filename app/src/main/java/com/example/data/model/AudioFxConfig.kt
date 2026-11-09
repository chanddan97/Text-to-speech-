package com.example.data.model

enum class BgmPreset(val displayName: String, val description: String) {
    CINEMATIC_PIANO_AM(
        "Cinematic Piano (A-Minor)",
        "Melancholy soft piano chords in A-minor with warm reverb decay"
    ),
    EMOTIONAL_STRINGS(
        "Dramatic Ambient Strings",
        "Slow swelling cinematic cello and sustained atmospheric drone"
    ),
    AMBIENT_PAD(
        "Deep Ethereal Pad",
        "Warm low-frequency cinematic synthesizer pad"
    ),
    NONE(
        "No Background Music (Voice Only)",
        "Dry solo dialogue without backing music"
    )
}

data class AudioFxConfig(
    val bassBoostDb: Float = 6.0f, // 0 to 12 dB shelf boost
    val reverbWetPercent: Float = 0.28f, // 0% to 100% room reverb
    val voiceVolume: Float = 1.0f, // 0.0 to 1.5
    val bgmVolume: Float = 0.35f, // 0.0 to 1.0
    val duckingDepthDb: Float = -14.0f, // -6 dB to -24 dB ducking during dialogue
    val bgmPreset: BgmPreset = BgmPreset.CINEMATIC_PIANO_AM,
    val duckingAttackMs: Int = 120,
    val duckingReleaseMs: Int = 450
)
