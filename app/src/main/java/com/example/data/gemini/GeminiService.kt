package com.example.data.gemini

import com.example.data.audio.WavAudioUtil
import com.example.data.model.EmotionPreset
import com.example.data.model.SceneBlock
import com.example.data.model.VoiceActor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    companion object {
        const val MODEL_NAME = "gemini-2.5-flash"
        const val FALLBACK_MODEL = "gemini-2.0-flash"
    }

    /**
     * Directs raw Bengali text into sequential scene blocks with micro-pauses,
     * voice actors, emotion presets, and pause durations.
     */
    suspend fun directStory(
        apiKey: String,
        rawStory: String
    ): Result<List<SceneBlock>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            // Intelligent fallback parser for offline/no-key mode
            return@withContext Result.success(parseStoryLocally(rawStory))
        }

        val prompt = """
You are an expert Bengali Cinematic Voiceover Director for CineVoice Studio Pro.
Analyze the following raw Bengali story or dialogue and break it into a sequence of dramatic scene blocks.

Story:
\"\"\"
$rawStory
\"\"\"

For each scene block, determine:
1. "title": A short evocative title (e.g., "Scene 1: Confrontation", "Scene 2: Heartbroken Whisper").
2. "text": Bengali text rewritten for high-impact vocal delivery with cinematic micro-pauses explicitly marked using '...', '[deep sigh]', '[whisper]', '[gasp]', or '[pause]'. Keep authentic Bengali spelling.
3. "emotion": Exactly one of ["angry_shout", "deep_story", "heartbroken_cry", "hospital_whisper", "mother_emotional"].
4. "voiceActor": Exactly one of ["Charon", "Fenrir", "Puck", "Orus", "Kore", "Aoede"].
   - Charon: Deep authoritative male narrator
   - Fenrir: Fierce, aggressive, confrontational male
   - Puck: Youthful, vibrant male
   - Orus: Resonant, elderly wise male
   - Kore: Gentle, warm emotional female
   - Aoede: Lyrical, tragic, dramatic female
5. "pauseSeconds": A recommended pause duration in seconds after this line (between 0.4 and 3.0).

Return ONLY a valid JSON array of objects with the exact keys:
[
  {
    "title": "Scene 1: ...",
    "text": "...",
    "emotion": "deep_story",
    "voiceActor": "Charon",
    "pauseSeconds": 1.2
  }
]
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("responseMimeType", "application/json")
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    // Try fallback model if 2.5-flash encounters access issue
                    return@withContext tryFallbackModel(apiKey, prompt, rawStory)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.getJSONObject(0)?.optString("text") ?: ""
                    val scenes = parseDirectorJson(text)
                    if (scenes.isNotEmpty()) {
                        return@withContext Result.success(scenes)
                    }
                }
                Result.success(parseStoryLocally(rawStory))
            }
        } catch (e: Exception) {
            // Graceful fallback to local intelligent director on network error
            Result.success(parseStoryLocally(rawStory))
        }
    }

    private fun tryFallbackModel(apiKey: String, prompt: String, rawStory: String): Result<List<SceneBlock>> {
        return try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })
            }
            val fallbackUrl = "https://generativelanguage.googleapis.com/v1beta/models/$FALLBACK_MODEL:generateContent?key=$apiKey"
            val req = Request.Builder()
                .url(fallbackUrl)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            client.newCall(req).execute().use { res ->
                val body = res.body?.string() ?: ""
                if (res.isSuccessful) {
                    val root = JSONObject(body)
                    val text = root.getJSONArray("candidates")
                        .getJSONObject(0).getJSONObject("content")
                        .getJSONArray("parts").getJSONObject(0).getString("text")
                    val scenes = parseDirectorJson(text)
                    if (scenes.isNotEmpty()) return Result.success(scenes)
                }
                Result.success(parseStoryLocally(rawStory))
            }
        } catch (_: Throwable) {
            Result.success(parseStoryLocally(rawStory))
        }
    }

    private fun parseDirectorJson(rawJson: String): List<SceneBlock> {
        val cleanJson = rawJson.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val list = mutableListOf<SceneBlock>()
        try {
            val array = JSONArray(cleanJson)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val title = item.optString("title", "Scene ${i + 1}")
                val text = item.optString("text", "")
                val emotionId = item.optString("emotion", "deep_story")
                val voiceName = item.optString("voiceActor", "Charon")
                val pause = item.optDouble("pauseSeconds", 1.0).toFloat().coerceIn(0.2f, 3.5f)

                list.add(
                    SceneBlock(
                        orderIndex = i + 1,
                        title = title,
                        text = text,
                        emotion = EmotionPreset.fromId(emotionId),
                        voiceActor = VoiceActor.fromName(voiceName),
                        silencePauseSeconds = pause
                    )
                )
            }
        } catch (_: Exception) {
            // ignore
        }
        return list
    }

    /**
     * Local intelligent Bengali story parser used offline or when API key isn't provided.
     */
    fun parseStoryLocally(rawStory: String): List<SceneBlock> {
        val cleaned = rawStory.trim()
        if (cleaned.isEmpty()) return emptyList()

        // Split by Bengali full stop (।) or exclamation (!) or question (?) or newline
        val rawSentences = cleaned.split(Regex("[।!?\\n]+"))
            .map { it.trim() }
            .filter { it.length >= 3 }

        if (rawSentences.isEmpty()) {
            return listOf(
                SceneBlock(
                    orderIndex = 1,
                    title = "Scene 1: Introduction",
                    text = cleaned,
                    emotion = EmotionPreset.DEEP_STORY,
                    voiceActor = VoiceActor.CHARON,
                    silencePauseSeconds = 1.0f
                )
            )
        }

        return rawSentences.mapIndexed { index, sentence ->
            val (emotion, actor) = inferEmotionAndActor(sentence, index)
            val enhancedText = addMicroPauses(sentence, emotion)
            SceneBlock(
                orderIndex = index + 1,
                title = "Scene ${index + 1}: ${emotion.displayName}",
                text = enhancedText,
                emotion = emotion,
                voiceActor = actor,
                silencePauseSeconds = when (emotion) {
                    EmotionPreset.HOSPITAL_WHISPER -> 1.8f
                    EmotionPreset.HEARTBROKEN_CRY -> 1.5f
                    EmotionPreset.MOTHER_EMOTIONAL -> 1.2f
                    EmotionPreset.ANGRY_SHOUT -> 0.6f
                    EmotionPreset.DEEP_STORY -> 1.0f
                }
            )
        }
    }

    private fun inferEmotionAndActor(text: String, index: Int): Pair<EmotionPreset, VoiceActor> {
        val lower = text.lowercase()
        return when {
            lower.contains("মা") || lower.contains("খোকা") || lower.contains("বাবু") || lower.contains("বাছা") ->
                Pair(EmotionPreset.MOTHER_EMOTIONAL, VoiceActor.KORE)
            lower.contains("কান্না") || lower.contains("বেদনা") || lower.contains("ছেড়ে") || lower.contains("মৃত্যু") || lower.contains("ব্যথা") ->
                Pair(EmotionPreset.HEARTBROKEN_CRY, VoiceActor.AOEDE)
            lower.contains("চিৎকার") || lower.contains("ক্ষমা নেই") || lower.contains("প্রতিশোধ") || lower.contains("শয়তান") || lower.contains("রুখো") ->
                Pair(EmotionPreset.ANGRY_SHOUT, VoiceActor.FENRIR)
            lower.contains("হাসপাতাল") || lower.contains("নিস্তব্ধ") || lower.contains("চুপিচুপি") || lower.contains("ফিসফিস") || lower.contains("রাত্রি") ->
                Pair(EmotionPreset.HOSPITAL_WHISPER, VoiceActor.PUCK)
            index % 3 == 0 -> Pair(EmotionPreset.DEEP_STORY, VoiceActor.CHARON)
            index % 3 == 1 -> Pair(EmotionPreset.HEARTBROKEN_CRY, VoiceActor.AOEDE)
            else -> Pair(EmotionPreset.DEEP_STORY, VoiceActor.ORUS)
        }
    }

    private fun addMicroPauses(sentence: String, emotion: EmotionPreset): String {
        return when (emotion) {
            EmotionPreset.HEARTBROKEN_CRY -> {
                if (!sentence.contains("...")) "$sentence... [deep sigh]" else sentence
            }
            EmotionPreset.HOSPITAL_WHISPER -> {
                if (!sentence.contains("[")) "[whisper] $sentence..." else sentence
            }
            EmotionPreset.ANGRY_SHOUT -> {
                sentence.replace(",", "—").replace("...", "!")
            }
            EmotionPreset.MOTHER_EMOTIONAL -> {
                "$sentence... [tender pause]"
            }
            EmotionPreset.DEEP_STORY -> {
                if (!sentence.endsWith("...")) "$sentence..." else sentence
            }
        }
    }

    /**
     * Performs a live authentication ping and model availability check.
     */
    suspend fun pingModel(apiKey: String): Result<Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        val url = "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - start
                if (response.isSuccessful) {
                    Result.success(duration)
                } else {
                    Result.failure(Exception("API returned HTTP ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Synthesizes emotional speech audio via Gemini 2.0 Flash Audio output.
     */
    suspend fun synthesizeSpeechAudio(
        apiKey: String,
        text: String,
        voiceActor: VoiceActor,
        emotion: EmotionPreset
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("No API key configured"))

        val geminiVoiceName = when (voiceActor) {
            VoiceActor.CHARON -> "Charon"
            VoiceActor.FENRIR -> "Fenrir"
            VoiceActor.PUCK -> "Puck"
            VoiceActor.ORUS -> "Charon"
            VoiceActor.KORE -> "Kore"
            VoiceActor.AOEDE -> "Aoede"
        }

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Read this Bengali dialogue clearly with deep ${emotion.displayName} emotion: $text")
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseModalities", JSONArray().apply { put("AUDIO") })
                put("speechConfig", JSONObject().apply {
                    put("voiceConfig", JSONObject().apply {
                        put("prebuiltVoiceConfig", JSONObject().apply {
                            put("voiceName", geminiVoiceName)
                        })
                    })
                })
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
        val req = Request.Builder()
            .url(url)
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(req).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Gemini audio returned HTTP ${response.code}"))
                }
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val parts = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val inlineData = parts.getJSONObject(0).optJSONObject("inlineData")
                        val base64Data = inlineData?.optString("data") ?: ""
                        if (base64Data.isNotBlank()) {
                            val audioBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                            val mime = inlineData?.optString("mimeType") ?: ""
                            val wavBytes = if (mime.contains("pcm", ignoreCase = true) || !isWavFile(audioBytes)) {
                                WavAudioUtil.wrapPcmToWav(audioBytes, 24000, 1)
                            } else {
                                audioBytes
                            }
                            return@withContext Result.success(wavBytes)
                        }
                    }
                }
                Result.failure(Exception("No audio bytes received from Gemini"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isWavFile(bytes: ByteArray): Boolean {
        if (bytes.size < 12) return false
        return bytes[0] == 'R'.code.toByte() &&
               bytes[1] == 'I'.code.toByte() &&
               bytes[2] == 'F'.code.toByte() &&
               bytes[3] == 'F'.code.toByte()
    }
}
