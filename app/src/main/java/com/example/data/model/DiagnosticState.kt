package com.example.data.model

enum class CheckStatus {
    IDLE,
    CHECKING,
    PASS,
    FAIL
}

data class DiagnosticStep(
    val name: String,
    val description: String,
    val status: CheckStatus = CheckStatus.IDLE,
    val details: String = "",
    val latencyMs: Long? = null
)

data class DiagnosticState(
    val isRunning: Boolean = false,
    val internetStep: DiagnosticStep = DiagnosticStep(
        name = "1. Internet Connection",
        description = "Check active network socket and DNS reachability"
    ),
    val authStep: DiagnosticStep = DiagnosticStep(
        name = "2. Authentication",
        description = "Validate Google AI Studio API key format and permissions"
    ),
    val modelStep: DiagnosticStep = DiagnosticStep(
        name = "3. Model Availability",
        description = "Verify gemini-2.5-flash and audio synthesis endpoints"
    ),
    val ttsStep: DiagnosticStep = DiagnosticStep(
        name = "4. TTS Synthesis",
        description = "Generate Bengali test token and acoustic PCM buffer"
    ),
    val latencyStep: DiagnosticStep = DiagnosticStep(
        name = "5. Audio Response & Latency",
        description = "Calculate end-to-end processing pipeline latency"
    ),
    val ramUsedMb: Long = 0,
    val ramTotalMb: Long = 0,
    val ramMaxMb: Long = 0,
    val audioSampleRate: Int = 24000,
    val audioBufferSize: Int = 4096,
    val overallResult: String? = null,
    val lastScanTimestamp: Long? = null
)
