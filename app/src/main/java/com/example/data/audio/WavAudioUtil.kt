package com.example.data.audio

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object WavAudioUtil {

    const val DEFAULT_SAMPLE_RATE = 24000
    const val BITS_PER_SAMPLE = 16
    const val CHANNELS = 1 // Mono for voice narration

    /**
     * Creates a standard 44-byte WAV header for PCM 16-bit audio.
     */
    fun createWavHeader(
        pcmDataSize: Int,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        channels: Int = CHANNELS,
        bitsPerSample: Int = BITS_PER_SAMPLE
    ): ByteArray {
        val totalDataLen = pcmDataSize + 36
        val byteRate = sampleRate * channels * (bitsPerSample / 8)
        val blockAlign = (channels * (bitsPerSample / 8)).toShort()

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        header.put("RIFF".toByteArray(Charsets.US_ASCII))
        header.putInt(totalDataLen)
        header.put("WAVE".toByteArray(Charsets.US_ASCII))

        // Format chunk
        header.put("fmt ".toByteArray(Charsets.US_ASCII))
        header.putInt(16) // Subchunk1Size for PCM
        header.putShort(1.toShort()) // AudioFormat 1 = PCM
        header.putShort(channels.toShort())
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(blockAlign)
        header.putShort(bitsPerSample.toShort())

        // Data chunk
        header.put("data".toByteArray(Charsets.US_ASCII))
        header.putInt(pcmDataSize)

        return header.array()
    }

    /**
     * Wraps raw 16-bit PCM bytes into a complete WAV byte array.
     */
    fun wrapPcmToWav(
        pcmBytes: ByteArray,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        channels: Int = CHANNELS
    ): ByteArray {
        val header = createWavHeader(pcmBytes.size, sampleRate, channels, BITS_PER_SAMPLE)
        val out = ByteArrayOutputStream(header.size + pcmBytes.size)
        out.write(header)
        out.write(pcmBytes)
        return out.toByteArray()
    }

    /**
     * Generates a byte array of silence PCM samples of given duration in seconds.
     */
    fun generateSilencePcm(
        durationSeconds: Float,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        channels: Int = CHANNELS
    ): ByteArray {
        val totalSamples = (durationSeconds * sampleRate * channels).toInt()
        val totalBytes = totalSamples * (BITS_PER_SAMPLE / 8)
        return ByteArray(totalBytes) // All zeroes = digital silence
    }

    /**
     * Reads PCM byte data out of a standard WAV file (skipping the 44-byte header).
     */
    fun readPcmFromWav(wavFile: File): ByteArray {
        if (!wavFile.exists() || wavFile.length() < 44) return ByteArray(0)
        val fileBytes = FileInputStream(wavFile).use { it.readBytes() }
        return if (fileBytes.size > 44) {
            fileBytes.copyOfRange(44, fileBytes.size)
        } else {
            ByteArray(0)
        }
    }

    /**
     * Writes raw PCM with WAV header to a target file.
     */
    fun writePcmToWavFile(
        outputFile: File,
        pcmBytes: ByteArray,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        channels: Int = CHANNELS
    ) {
        val wavBytes = wrapPcmToWav(pcmBytes, sampleRate, channels)
        outputFile.parentFile?.mkdirs()
        FileOutputStream(outputFile).use { fos ->
            fos.write(wavBytes)
            fos.flush()
        }
    }
}
