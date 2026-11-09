package com.example.data.model

import java.util.UUID

data class SceneBlock(
    val id: String = UUID.randomUUID().toString(),
    val orderIndex: Int = 1,
    val title: String = "Scene $orderIndex",
    val text: String = "",
    val emotion: EmotionPreset = EmotionPreset.DEEP_STORY,
    val voiceActor: VoiceActor = VoiceActor.CHARON,
    val silencePauseSeconds: Float = 1.0f,
    val audioFilePath: String? = null,
    val durationMs: Long = 0L,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null
)
