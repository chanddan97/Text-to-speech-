package com.example.data.audio

import com.example.data.model.BgmPreset
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object AmbientBgmSynthesizer {

    /**
     * Synthesizes 16-bit PCM audio samples matching the specified duration in seconds.
     */
    fun synthesizeBgm(
        preset: BgmPreset,
        durationSeconds: Float,
        sampleRate: Int = WavAudioUtil.DEFAULT_SAMPLE_RATE
    ): ByteArray {
        if (preset == BgmPreset.NONE || durationSeconds <= 0f) {
            return ByteArray(0)
        }

        val totalSamples = (durationSeconds * sampleRate).toInt()
        val shortBuffer = ShortArray(totalSamples)

        when (preset) {
            BgmPreset.CINEMATIC_PIANO_AM -> generatePianoAmProgression(shortBuffer, sampleRate)
            BgmPreset.EMOTIONAL_STRINGS -> generateEmotionalStrings(shortBuffer, sampleRate)
            BgmPreset.AMBIENT_PAD -> generateAmbientDrone(shortBuffer, sampleRate)
            BgmPreset.NONE -> {}
        }

        // Convert ShortArray (16-bit PCM) to little-endian ByteArray
        val byteBuffer = ByteArray(totalSamples * 2)
        for (i in 0 until totalSamples) {
            val sample = shortBuffer[i].toInt()
            byteBuffer[i * 2] = (sample and 0xFF).toByte()
            byteBuffer[i * 2 + 1] = ((sample shr 8) and 0xFF).toByte()
        }
        return byteBuffer
    }

    private fun generatePianoAmProgression(buffer: ShortArray, sampleRate: Int) {
        val chordDurationSec = 3.5 // 3.5 seconds per chord
        val samplesPerChord = (chordDurationSec * sampleRate).toInt()

        // Chords: Am -> Fmaj7 -> Cmaj -> Em/G
        val chords = listOf(
            floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f), // Am (A3, C4, E4, A4)
            floatArrayOf(174.61f, 220.00f, 261.63f, 349.23f), // F (F3, A3, C4, F4)
            floatArrayOf(130.81f, 196.00f, 261.63f, 329.63f), // C (C3, G3, C4, E4)
            floatArrayOf(164.81f, 196.00f, 246.94f, 329.63f)  // Em (E3, G3, B3, E4)
        )

        val totalSamples = buffer.size
        for (i in 0 until totalSamples) {
            val chordIdx = (i / samplesPerChord) % chords.size
            val notes = chords[chordIdx]
            val localTime = (i % samplesPerChord).toDouble() / sampleRate
            val chordProgress = localTime / chordDurationSec

            // Piano-like decay envelope: attack then exponential decay
            val decay = exp(-1.2 * localTime) * (1.0 - exp(-20.0 * localTime))

            var sampleSum = 0.0
            for ((noteIndex, freq) in notes.withIndex()) {
                // Stagger note onsets slightly for natural arpeggiated piano feel
                val noteOffset = noteIndex * 0.12
                if (localTime >= noteOffset) {
                    val noteTime = localTime - noteOffset
                    val noteDecay = exp(-1.4 * noteTime)
                    // Fundamental + subtle harmonics (pure piano resonance)
                    val s1 = sin(2.0 * PI * freq * noteTime)
                    val s2 = 0.45 * sin(4.0 * PI * freq * noteTime)
                    val s3 = 0.18 * sin(6.0 * PI * freq * noteTime)
                    sampleSum += (s1 + s2 + s3) * noteDecay * 0.22
                }
            }

            // Warm subtle tape saturation clamp
            val normalized = (sampleSum * decay * 14000.0).coerceIn(-30000.0, 30000.0)
            buffer[i] = normalized.toInt().toShort()
        }
    }

    private fun generateEmotionalStrings(buffer: ShortArray, sampleRate: Int) {
        // Sustained cello and dramatic violin pad
        val fundamentalFreq = 110.0 // A2
        val fifthFreq = 164.81 // E3
        val thirdFreq = 220.0 // A3

        val totalSamples = buffer.size
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // LFO vibrato (5.5 Hz)
            val vibrato = 1.0 + 0.012 * sin(2.0 * PI * 5.5 * t)
            val s1 = sin(2.0 * PI * fundamentalFreq * vibrato * t)
            val s2 = 0.6 * sin(2.0 * PI * fifthFreq * vibrato * t)
            val s3 = 0.4 * sin(2.0 * PI * thirdFreq * vibrato * t)
            val pad = (s1 + s2 + s3) * 0.28 * 12000.0
            buffer[i] = pad.coerceIn(-28000.0, 28000.0).toInt().toShort()
        }
    }

    private fun generateAmbientDrone(buffer: ShortArray, sampleRate: Int) {
        val totalSamples = buffer.size
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val drone1 = sin(2.0 * PI * 55.0 * t) // Low A1
            val drone2 = 0.5 * sin(2.0 * PI * 110.0 * t)
            val sweep = 0.5 + 0.5 * sin(2.0 * PI * 0.15 * t) // Slow filter sweep
            val mixed = (drone1 + drone2 * sweep) * 0.3 * 13000.0
            buffer[i] = mixed.coerceIn(-28000.0, 28000.0).toInt().toShort()
        }
    }
}
