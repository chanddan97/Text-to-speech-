package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DiagnosticStepRow
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGoldGlow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.StatusPass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun DiagnosticScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val diagnosticState by viewModel.diagnosticState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Self-Diagnostic Scanner",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Module D: Real-time RAM heap, Audio engine state, and API health checks",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware & RAM Heap Metrics
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2838)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "System RAM Heap",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "JVM Runtime Memory Allocation",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "${diagnosticState.ramUsedMb} MB / ${diagnosticState.ramMaxMb} MB",
                        color = Color(0xFF60A5FA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val ramProgress = if (diagnosticState.ramMaxMb > 0) {
                    (diagnosticState.ramUsedMb.toFloat() / diagnosticState.ramMaxMb).coerceIn(0f, 1f)
                } else 0.1f

                LinearProgressIndicator(
                    progress = { ramProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF60A5FA),
                    trackColor = ObsidianSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Used Heap: ${diagnosticState.ramUsedMb} MB",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Allocated: ${diagnosticState.ramTotalMb} MB",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audio Engine Hardware State
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF231A05)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CinematicGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Audio Engine Hardware State",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PCM 24kHz 16-Bit RIFF Studio Pipeline",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AudioMetricBadge(label = "Sample Rate", value = "${diagnosticState.audioSampleRate} Hz", modifier = Modifier.weight(1f))
                    AudioMetricBadge(label = "Channels", value = "1 (Mono Speech)", modifier = Modifier.weight(1f))
                    AudioMetricBadge(label = "Resolution", value = "16-Bit PCM", modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live 5-Step Verification Checklist (Per PRD)
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Live Verification Checklist",
                    color = CinematicGoldGlow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "End-to-end socket, authentication, model & synthesis verification",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianSurfaceElevated)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DiagnosticStepRow(diagnosticState.internetStep)
                    DiagnosticStepRow(diagnosticState.authStep)
                    DiagnosticStepRow(diagnosticState.modelStep)
                    DiagnosticStepRow(diagnosticState.ttsStep)
                    DiagnosticStepRow(diagnosticState.latencyStep)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.runDiagnosticScan() },
                        enabled = !diagnosticState.isRunning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinematicGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("run_scan_button")
                    ) {
                        if (diagnosticState.isRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Running Scan...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Full Scan", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.copyDiagnosticReport() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CinematicGold
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CinematicGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("copy_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Report", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun AudioMetricBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = label, color = TextMuted, fontSize = 10.sp)
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
