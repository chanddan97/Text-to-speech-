package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmotionPreset
import com.example.data.model.SceneBlock
import com.example.data.model.VoiceActor
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGoldGlow
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.StatusFail
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SceneBlockCard(
    scene: SceneBlock,
    isPlaying: Boolean,
    onUpdate: (SceneBlock) -> Unit,
    onSynthesize: () -> Unit,
    onTogglePlay: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedVoiceSelect by remember { mutableStateOf(false) }

    Surface(
        color = ObsidianSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) CinematicGold else ObsidianBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("scene_card_${scene.orderIndex}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Title + Reorder + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(scene.emotion.badgeColor.copy(alpha = 0.2f))
                            .border(1.dp, scene.emotion.badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "#${scene.orderIndex} ${scene.title}",
                            color = scene.emotion.badgeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (scene.audioFilePath != null) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF14532D))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "WAV Ready",
                                color = Color(0xFF4ADE80),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onMoveUp,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("move_up_${scene.orderIndex}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Move Scene Up",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("move_down_${scene.orderIndex}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Move Scene Down",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_${scene.orderIndex}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Scene",
                            tint = StatusFail.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bengali Dialogue Text
            OutlinedTextField(
                value = scene.text,
                onValueChange = { onUpdate(scene.copy(text = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scene_text_${scene.orderIndex}"),
                label = { Text("Bengali Dialogue & Directions") },
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = CinematicGold,
                    unfocusedBorderColor = ObsidianBorder,
                    cursorColor = CinematicGold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Micro-Pause Quick Tags
            Text(
                text = "Insert Micro-Pause / Stage Direction:",
                color = TextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("...", "[deep sigh]", "[whisper]", "[gasp]", "[pause]").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianSurfaceVariant)
                            .clickable {
                                val newText = if (scene.text.endsWith(" ")) {
                                    scene.text + tag + " "
                                } else {
                                    scene.text + " " + tag + " "
                                }
                                onUpdate(scene.copy(text = newText))
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+ $tag",
                            color = CinematicGoldGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Emotion Preset Selector Chips
            Text(
                text = "Emotion Preset (${scene.emotion.bengaliLabel}):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EmotionPreset.entries.forEach { preset ->
                    val isSelected = scene.emotion == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) preset.badgeColor else ObsidianSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdate(scene.copy(emotion = preset)) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = preset.displayName,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Voice Actor Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Voice Actor: ${scene.voiceActor.actorName} (${scene.voiceActor.title})",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (expandedVoiceSelect) "Less" else "Change",
                    color = CinematicGold,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { expandedVoiceSelect = !expandedVoiceSelect }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                VoiceActor.entries.forEach { actor ->
                    val isSelected = scene.voiceActor == actor
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CinematicGold else ObsidianSurfaceVariant)
                            .clickable { onUpdate(scene.copy(voiceActor = actor)) }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${actor.actorName} (${actor.gender.take(1)})",
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            if (expandedVoiceSelect) {
                Text(
                    text = scene.voiceActor.description,
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Silence Pause Duration Slider (0.2s to 3.5s) per PRD
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Silence Pause After Scene:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = String.format("%.1fs", scene.silencePauseSeconds),
                    color = CinematicGoldGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = scene.silencePauseSeconds,
                onValueChange = { onUpdate(scene.copy(silencePauseSeconds = it)) },
                valueRange = 0.2f..3.5f,
                steps = 32,
                colors = SliderDefaults.colors(
                    thumbColor = CinematicGold,
                    activeTrackColor = CinematicGold,
                    inactiveTrackColor = ObsidianBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pause_slider_${scene.orderIndex}")
            )

            // Scene Playback & Individual Synthesis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play / Preview Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPlaying) CinematicGold else ObsidianSurfaceVariant)
                        .clickable(onClick = onTogglePlay)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("play_scene_${scene.orderIndex}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play Preview",
                            tint = if (isPlaying) Color.Black else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "Playing" else if (scene.audioFilePath != null) "Preview" else "Synthesize & Play",
                            color = if (isPlaying) Color.Black else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Individual Synthesize Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianBorder.copy(alpha = 0.5f))
                        .clickable(enabled = !scene.isGenerating, onClick = onSynthesize)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("synth_scene_${scene.orderIndex}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (scene.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = CinematicGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rendering...", color = CinematicGold, fontSize = 11.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "Regenerate Line",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Regenerate", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
