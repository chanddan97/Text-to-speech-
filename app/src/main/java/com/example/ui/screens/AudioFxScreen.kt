package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.data.model.BgmPreset
import com.example.ui.theme.CinematicAmber
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGoldGlow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AudioFxScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val fxConfig by viewModel.audioFxConfig.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Cinematic Audio Post-Processing",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Module C: Hardware DSP shelf filters, room acoustics & auto-ducking mixer",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Warm Bass Boost Shelf Filter
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
                                text = "Warm Bass Boost",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "120Hz Low-Shelf Filter for broadcast resonance",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "+${String.format("%.1f", fxConfig.bassBoostDb)} dB",
                        color = CinematicGoldGlow,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = fxConfig.bassBoostDb,
                    onValueChange = { viewModel.updateBassBoost(it) },
                    valueRange = 0.0f..12.0f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = CinematicGold,
                        activeTrackColor = CinematicGold,
                        inactiveTrackColor = ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("bass_boost_slider")
                )

                Text(
                    text = "Adds deep cinematic weight and authoritative presence to male and female voices.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Subtle Room Reverb
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
                                .background(Color(0xFF1E1A33)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SurroundSound,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Subtle Room Reverb",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Schroeder-Moorer Acoustic Studio Presence",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "${(fxConfig.reverbWetPercent * 100).toInt()}% Wet",
                        color = Color(0xFFA78BFA),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = fxConfig.reverbWetPercent,
                    onValueChange = { viewModel.updateReverb(it) },
                    valueRange = 0.0f..0.8f,
                    steps = 16,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFA78BFA),
                        activeTrackColor = Color(0xFFA78BFA),
                        inactiveTrackColor = ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("reverb_slider")
                )

                Text(
                    text = "Soft room reflections create natural emotional intimacy without muddying dialogue.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. BGM Auto-Ducking Mixer
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF132A1E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BGM Auto-Ducking Mixer",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ducks music under speech; swells up during micro-pauses",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Background Music Soundtrack:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                BgmPreset.entries.forEach { preset ->
                    val isSelected = fxConfig.bgmPreset == preset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ObsidianSurfaceVariant else Color.Transparent)
                            .clickable { viewModel.selectBgmPreset(preset) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (isSelected) Color(0xFF34D399) else ObsidianBorder)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = preset.displayName,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = preset.description,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ducking Depth Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dialogue Ducking Depth:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${fxConfig.duckingDepthDb.toInt()} dB",
                        color = Color(0xFF34D399),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = fxConfig.duckingDepthDb,
                    onValueChange = { viewModel.updateDuckingDepth(it) },
                    valueRange = -24.0f..-6.0f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF34D399),
                        activeTrackColor = Color(0xFF34D399),
                        inactiveTrackColor = ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ducking_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // BGM Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "BGM Master Level:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(fxConfig.bgmVolume * 100).toInt()}%",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = fxConfig.bgmVolume,
                    onValueChange = { viewModel.updateBgmVolume(it) },
                    valueRange = 0.0f..1.0f,
                    steps = 20,
                    colors = SliderDefaults.colors(
                        thumbColor = CinematicAmber,
                        activeTrackColor = CinematicAmber,
                        inactiveTrackColor = ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("bgm_vol_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Voice Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Voice Dialogue Level:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(fxConfig.voiceVolume * 100).toInt()}%",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = fxConfig.voiceVolume,
                    onValueChange = { viewModel.updateVoiceVolume(it) },
                    valueRange = 0.0f..1.5f,
                    steps = 30,
                    colors = SliderDefaults.colors(
                        thumbColor = CinematicGold,
                        activeTrackColor = CinematicGold,
                        inactiveTrackColor = ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("voice_vol_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Audition Button
        Button(
            onClick = { viewModel.renderMasterAudio() },
            colors = ButtonDefaults.buttonColors(
                containerColor = CinematicGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("apply_fx_button")
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Apply DSP & Audition Master Mix", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
