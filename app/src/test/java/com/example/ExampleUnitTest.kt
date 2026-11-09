package com.example

import com.example.data.audio.AmbientBgmSynthesizer
import com.example.data.audio.DspAudioProcessor
import com.example.data.audio.WavAudioUtil
import com.example.data.gemini.GeminiService
import com.example.data.model.AudioFxConfig
import com.example.data.model.BgmPreset
import com.example.data.model.EmotionPreset
import com.example.data.model.VoiceActor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testWavHeaderCreation() {
        val pcmSize = 48000
        val header = WavAudioUtil.createWavHeader(pcmSize)
        assertEquals(44, header.size)
        // Check RIFF magic
        assertEquals('R'.code.toByte(), header[0])
        assertEquals('I'.code.toByte(), header[1])
        assertEquals('F'.code.toByte(), header[2])
        assertEquals('F'.code.toByte(), header[3])
        // Check WAVE magic
        assertEquals('W'.code.toByte(), header[8])
        assertEquals('A'.code.toByte(), header[9])
        assertEquals('V'.code.toByte(), header[10])
        assertEquals('E'.code.toByte(), header[11])
    }

    @Test
    fun testSilenceBufferGeneration() {
        val durationSec = 1.0f
        val silence = WavAudioUtil.generateSilencePcm(durationSec)
        val expectedBytes = (1.0f * WavAudioUtil.DEFAULT_SAMPLE_RATE * 2).toInt()
        assertEquals(expectedBytes, silence.size)
        // All bytes should be zero
        assertTrue(silence.all { it == 0.toByte() })
    }

    @Test
    fun testProceduralBgmSynthesis() {
        val duration = 2.0f
        val bgmPcm = AmbientBgmSynthesizer.synthesizeBgm(
            preset = BgmPreset.CINEMATIC_PIANO_AM,
            durationSeconds = duration
        )
        val expectedBytes = (duration * WavAudioUtil.DEFAULT_SAMPLE_RATE * 2).toInt()
        assertEquals(expectedBytes, bgmPcm.size)
    }

    @Test
    fun testDspMixerWithAutoDucking() {
        val voicePcm = WavAudioUtil.generateSilencePcm(1.0f)
        val bgmPcm = AmbientBgmSynthesizer.synthesizeBgm(BgmPreset.CINEMATIC_PIANO_AM, 1.0f)
        val config = AudioFxConfig(
            bassBoostDb = 6.0f,
            reverbWetPercent = 0.3f,
            bgmVolume = 0.4f,
            voiceVolume = 1.0f,
            duckingDepthDb = -14.0f
        )
        val mixed = DspAudioProcessor.processAndMix(voicePcm, bgmPcm, config)
        assertTrue(mixed.isNotEmpty())
    }

    @Test
    fun testLocalBengaliStoryParser() {
        val service = GeminiService()
        val bengaliStory = "দেবদাস বলল, 'পারু, আজ শেষবারের মতো এলাম।' পারুর চোখে তখন জল।"
        val scenes = service.parseStoryLocally(bengaliStory)
        assertTrue(scenes.isNotEmpty())
        assertNotNull(scenes[0].text)
        assertNotNull(scenes[0].emotion)
        assertNotNull(scenes[0].voiceActor)
    }

    @Test
    fun testEmotionAndVoiceActorPresets() {
        val actor = VoiceActor.CHARON
        assertEquals("Charon", actor.actorName)
        assertTrue(actor.pitchMultiplier < 1.0f) // Deep baritone pitch

        val emotion = EmotionPreset.ANGRY_SHOUT
        assertEquals("angry_shout", emotion.presetId)
        assertTrue(emotion.rateShift > 1.0f) // Faster rate for angry shouting
    }
}
