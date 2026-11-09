package com.example.data.audio

import com.example.data.model.AudioFxConfig
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object DspAudioProcessor {

    /**
     * Applies the complete cinematic DSP chain:
     * 1. Warm Bass Boost on Voice
     * 2. Subtle Room Reverb on Voice
     * 3. Volume Scaling
     * 4. Auto-Ducking Mix with BGM
     */
    fun processAndMix(
        voicePcmBytes: ByteArray,
        bgmPcmBytes: ByteArray,
        config: AudioFxConfig,
        sampleRate: Int = WavAudioUtil.DEFAULT_SAMPLE_RATE
    ): ByteArray {
        val voiceSamples = bytesToShorts(voicePcmBytes)
        val bgmSamples = bytesToShorts(bgmPcmBytes)

        val totalLength = max(voiceSamples.size, bgmSamples.size)
        if (totalLength == 0) return ByteArray(0)

        // 1. Process Voice Samples (Bass Boost + Reverb)
        val processedVoice = FloatArray(totalLength)
        for (i in voiceSamples.indices) {
            processedVoice[i] = voiceSamples[i] / 32768.0f
        }

        // Apply Warm Bass Boost if enabled
        if (config.bassBoostDb > 0.1f) {
            applyLowShelfFilter(
                samples = processedVoice,
                gainDb = config.bassBoostDb,
                cutoffFreq = 140.0f,
                sampleRate = sampleRate.toFloat()
            )
        }

        // Apply Subtle Room Reverb if enabled
        if (config.reverbWetPercent > 0.05f) {
            applySchroederReverb(
                samples = processedVoice,
                wetRatio = config.reverbWetPercent,
                sampleRate = sampleRate
            )
        }

        // 2. Prepare BGM with Envelope Auto-Ducking
        val processedBgm = FloatArray(totalLength)
        for (i in bgmSamples.indices) {
            processedBgm[i] = bgmSamples[i] / 32768.0f
        }

        // Auto-Ducking calculation:
        // Voice envelope follower
        val attackCoeff = exp(-1.0f / ((config.duckingAttackMs / 1000.0f) * sampleRate))
        val releaseCoeff = exp(-1.0f / ((config.duckingReleaseMs / 1000.0f) * sampleRate))
        val duckMultiplier = 10.0f.pow(config.duckingDepthDb / 20.0f) // e.g. -14dB = ~0.20

        var voiceEnvelope = 0.0f
        val mixedSamples = ShortArray(totalLength)

        for (i in 0 until totalLength) {
            // Envelope follower on voice absolute amplitude
            val voiceAbs = abs(processedVoice[i])
            voiceEnvelope = if (voiceAbs > voiceEnvelope) {
                attackCoeff * voiceEnvelope + (1.0f - attackCoeff) * voiceAbs
            } else {
                releaseCoeff * voiceEnvelope + (1.0f - releaseCoeff) * voiceAbs
            }

            // Duck factor: 1.0 (full BGM) when quiet, drops down to duckMultiplier when voice speaking
            val duckActivity = (voiceEnvelope * 3.5f).coerceIn(0.0f, 1.0f)
            val currentBgmGain = (1.0f - duckActivity) * 1.0f + duckActivity * duckMultiplier

            val vSample = processedVoice[i] * config.voiceVolume
            val bSample = processedBgm[i] * config.bgmVolume * currentBgmGain

            // Sum and soft-saturation limiter to eliminate digital clipping
            val mixed = vSample + bSample
            val saturated = softLimit(mixed)
            mixedSamples[i] = (saturated * 32767.0f).toInt().coerceIn(-32768, 32767).toShort()
        }

        return shortsToBytes(mixedSamples)
    }

    /**
     * Biquad Low-Shelf filter for warm cinematic bass.
     */
    private fun applyLowShelfFilter(
        samples: FloatArray,
        gainDb: Float,
        cutoffFreq: Float,
        sampleRate: Float
    ) {
        val a = 10.0f.pow(gainDb / 40.0f)
        val w0 = 2.0f * PI.toFloat() * cutoffFreq / sampleRate
        val cosw0 = cos(w0)
        val sinw0 = sin(w0)
        val q = 0.707f // Butterworth damping
        val alpha = sinw0 / (2.0f * q)
        val sqrtA = sqrt(a)

        val b0 = a * ((a + 1.0f) - (a - 1.0f) * cosw0 + 2.0f * sqrtA * alpha)
        val b1 = 2.0f * a * ((a - 1.0f) - (a + 1.0f) * cosw0)
        val b2 = a * ((a + 1.0f) - (a - 1.0f) * cosw0 - 2.0f * sqrtA * alpha)
        val a0 = (a + 1.0f) + (a - 1.0f) * cosw0 + 2.0f * sqrtA * alpha
        val a1 = -2.0f * ((a - 1.0f) + (a + 1.0f) * cosw0)
        val a2 = (a + 1.0f) + (a - 1.0f) * cosw0 - 2.0f * sqrtA * alpha

        // Normalize coefficients
        val nb0 = b0 / a0
        val nb1 = b1 / a0
        val nb2 = b2 / a0
        val na1 = a1 / a0
        val na2 = a2 / a0

        var x1 = 0.0f
        var x2 = 0.0f
        var y1 = 0.0f
        var y2 = 0.0f

        for (i in samples.indices) {
            val x0 = samples[i]
            val y0 = nb0 * x0 + nb1 * x1 + nb2 * x2 - na1 * y1 - na2 * y2
            x2 = x1
            x1 = x0
            y2 = y1
            y1 = y0
            samples[i] = y0
        }
    }

    /**
     * Schroeder Reverb: 4 comb filters in parallel + 2 allpass filters in series.
     */
    private fun applySchroederReverb(
        samples: FloatArray,
        wetRatio: Float,
        sampleRate: Int
    ) {
        // Delays in ms scaled for sample rate
        val combDelays = intArrayOf(
            (0.0297f * sampleRate).toInt(),
            (0.0371f * sampleRate).toInt(),
            (0.0411f * sampleRate).toInt(),
            (0.0437f * sampleRate).toInt()
        )
        val combFeedback = floatArrayOf(0.74f, 0.72f, 0.70f, 0.68f)

        val combBuffers = combDelays.map { FloatArray(it) }
        val combIndices = IntArray(combDelays.size)

        // Process comb filters in parallel
        val combOutput = FloatArray(samples.size)
        for (i in samples.indices) {
            val input = samples[i]
            var sum = 0.0f
            for (c in combDelays.indices) {
                val buf = combBuffers[c]
                val delay = combDelays[c]
                val idx = combIndices[c]
                val delayedSample = buf[idx]
                buf[idx] = input + delayedSample * combFeedback[c]
                combIndices[c] = (idx + 1) % delay
                sum += delayedSample
            }
            combOutput[i] = sum * 0.25f
        }

        // Two series all-pass filters
        val allPassDelay = (0.0050f * sampleRate).toInt()
        val allPassBuffer = FloatArray(allPassDelay)
        var allPassIdx = 0
        val apGain = 0.5f

        for (i in combOutput.indices) {
            val input = combOutput[i]
            val delayed = allPassBuffer[allPassIdx]
            val v = input - apGain * delayed
            val out = delayed + apGain * v
            allPassBuffer[allPassIdx] = v
            allPassIdx = (allPassIdx + 1) % allPassDelay
            combOutput[i] = out
        }

        // Blend dry and wet
        val dryRatio = 1.0f - wetRatio
        for (i in samples.indices) {
            samples[i] = samples[i] * dryRatio + combOutput[i] * wetRatio
        }
    }

    private fun softLimit(x: Float): Float {
        // Smooth hyperbolic curve to avoid harsh clipping
        return when {
            x > 1.0f -> 1.0f - 1.0f / (x + 1.0f)
            x < -1.0f -> -1.0f + 1.0f / (-x + 1.0f)
            else -> x - (x * x * x) / 6.0f
        }
    }

    private fun bytesToShorts(bytes: ByteArray): ShortArray {
        val shorts = ShortArray(bytes.size / 2)
        for (i in shorts.indices) {
            val b0 = bytes[i * 2].toInt() and 0xFF
            val b1 = bytes[i * 2 + 1].toInt()
            shorts[i] = ((b1 shl 8) or b0).toShort()
        }
        return shorts
    }

    private fun shortsToBytes(shorts: ShortArray): ByteArray {
        val bytes = ByteArray(shorts.size * 2)
        for (i in shorts.indices) {
            val s = shorts[i].toInt()
            bytes[i * 2] = (s and 0xFF).toByte()
            bytes[i * 2 + 1] = ((s shr 8) and 0xFF).toByte()
        }
        return bytes
    }
}
