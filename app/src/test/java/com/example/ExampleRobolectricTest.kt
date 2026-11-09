package com.example

import com.example.data.audio.AmbientBgmSynthesizer
import com.example.data.audio.DspAudioProcessor
import com.example.data.audio.WavAudioUtil
import com.example.data.model.AudioFxConfig
import com.example.data.model.BgmPreset
import com.example.data.model.EmotionPreset
import com.example.data.model.SceneBlock
import com.example.data.model.VoiceActor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testCriticalUserJourneyStoryDirectorToWav() {
    // 1. Create sequential scene blocks
    val scene1 = SceneBlock(
      orderIndex = 1,
      title = "Scene 1: Introduction",
      text = "দেবদাস বলল, 'পারু, আজ শেষবারের মতো এলাম।' ... [deep sigh]",
      emotion = EmotionPreset.HEARTBROKEN_CRY,
      voiceActor = VoiceActor.CHARON,
      silencePauseSeconds = 1.2f
    )

    val scene2 = SceneBlock(
      orderIndex = 2,
      title = "Scene 2: Response",
      text = "পারু ফিসফিস করে বলল, 'ভালোবাসা কি কখনো ক্ষমা চায়?'",
      emotion = EmotionPreset.HOSPITAL_WHISPER,
      voiceActor = VoiceActor.AOEDE,
      silencePauseSeconds = 1.5f
    )

    assertEquals("Charon", scene1.voiceActor.actorName)
    assertEquals("Aoede", scene2.voiceActor.actorName)
    assertEquals(EmotionPreset.HEARTBROKEN_CRY, scene1.emotion)

    // 2. Synthesize BGM
    val bgmBytes = AmbientBgmSynthesizer.synthesizeBgm(
      preset = BgmPreset.CINEMATIC_PIANO_AM,
      durationSeconds = 2.0f
    )
    assertTrue(bgmBytes.isNotEmpty())

    // 3. Audio FX Processing & Auto-Ducking
    val voicePcm = WavAudioUtil.generateSilencePcm(2.0f)
    val config = AudioFxConfig(
      bassBoostDb = 6.0f,
      reverbWetPercent = 0.28f,
      voiceVolume = 1.0f,
      bgmVolume = 0.35f,
      duckingDepthDb = -14.0f
    )
    val masteredPcm = DspAudioProcessor.processAndMix(voicePcm, bgmBytes, config)
    assertTrue(masteredPcm.isNotEmpty())

    // 4. Wrap to WAV
    val wavBytes = WavAudioUtil.wrapPcmToWav(masteredPcm)
    assertEquals(masteredPcm.size + 44, wavBytes.size)
  }
}
