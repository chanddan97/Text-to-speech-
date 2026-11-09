package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.SceneBlockCard
import com.example.ui.components.WaveformVisualizer
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val storyInput by viewModel.storyInput.collectAsState()
    val projectTitle by viewModel.projectTitle.collectAsState()
    val sceneBlocks by viewModel.sceneBlocks.collectAsState()
    val isDirecting by viewModel.isDirecting.collectAsState()
    val batchProgress by viewModel.batchGeneratingProgress.collectAsState()
    val isRenderingMaster by viewModel.isRenderingMaster.collectAsState()
    val masterFile by viewModel.masterAudioFile.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()
    val activePlayingId by viewModel.audioEngine.activePlayingId.collectAsState()
    val currentPositionMs by viewModel.audioEngine.currentPositionMs.collectAsState()
    val totalDurationMs by viewModel.audioEngine.totalDurationMs.collectAsState()

    val playbackProgress = if (totalDurationMs > 0) {
        (currentPositionMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)
    } else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Project Title Input
            OutlinedTextField(
                value = projectTitle,
                onValueChange = { viewModel.setProjectTitle(it) },
                label = { Text("Project Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = CinematicGold,
                    unfocusedBorderColor = ObsidianBorder,
                    cursorColor = CinematicGold
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Selector Chips
            Text(
                text = "Bengali Story Presets:",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf(
                    "দেবদাস ও পার্বতী",
                    "মধ্যরাতের রহস্য",
                    "মায়ের শেষ চিঠি",
                    "বিপ্লবী আহ্বান"
                )
                presets.forEachIndexed { index, name ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianSurfaceVariant)
                            .clickable { viewModel.loadPreset(index) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("preset_$index")
                    ) {
                        Text(
                            text = name,
                            color = CinematicGoldGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Raw Bengali Story Input Card
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Module A: Raw Story / Dialogue",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Paste or type Bengali text. Auto Story Director will segment scenes & assign actors.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = storyInput,
                        onValueChange = { viewModel.setStoryInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("raw_story_input"),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CinematicGold,
                            unfocusedBorderColor = ObsidianBorder,
                            cursorColor = CinematicGold
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Direct Story Button
                    Button(
                        onClick = { viewModel.directStoryWithGemini() },
                        enabled = !isDirecting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinematicGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("direct_story_button")
                    ) {
                        if (isDirecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Directing with Gemini AI...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Direct Story with Gemini AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Batch Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Directed Scenes (${sceneBlocks.size})",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.addSceneBlock() },
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_scene_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = CinematicGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Scene", color = CinematicGold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.generateAllScenes() },
                        enabled = batchProgress == null && sceneBlocks.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C220E),
                            contentColor = CinematicGoldGlow
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CinematicGold.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("generate_all_button")
                    ) {
                        Text("Generate All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (batchProgress != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianSurfaceElevated)
                        .padding(10.dp)
                ) {
                    Text(
                        text = batchProgress ?: "",
                        color = CinematicGoldGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = CinematicGold,
                        trackColor = ObsidianBorder
                    )
                }
            }
        }

        // Sequential Scene Block Cards
        items(sceneBlocks, key = { it.id }) { scene ->
            SceneBlockCard(
                scene = scene,
                isPlaying = isPlaying && activePlayingId == scene.id,
                onUpdate = { viewModel.updateSceneBlock(it) },
                onSynthesize = { viewModel.synthesizeScene(scene.id) },
                onTogglePlay = { viewModel.togglePlayScene(scene) },
                onMoveUp = { viewModel.moveScene(scene.id, true) },
                onMoveDown = { viewModel.moveScene(scene.id, false) },
                onDelete = { viewModel.removeSceneBlock(scene.id) }
            )
        }

        // Master Audio Export Dock
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = ObsidianSurfaceElevated,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CinematicGold.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("master_export_dock")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Master Audio Render",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Combines scenes + micro-pauses + bass/reverb + ducked BGM",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (masterFile != null) {
                            IconButton(
                                onClick = { viewModel.shareMasterWav() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ObsidianSurfaceVariant)
                                    .testTag("share_master_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share WAV",
                                    tint = CinematicGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Waveform Visualizer
                    WaveformVisualizer(
                        isPlaying = isPlaying && activePlayingId == "master_track",
                        progress = playbackProgress
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%02d:%02d", (currentPositionMs / 1000) / 60, (currentPositionMs / 1000) % 60),
                            color = CinematicGoldGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = String.format("%02d:%02d", (totalDurationMs / 1000) / 60, (totalDurationMs / 1000) % 60),
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.renderMasterAudio() },
                            enabled = !isRenderingMaster && sceneBlocks.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CinematicGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("render_master_button")
                        ) {
                            if (isRenderingMaster) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rendering WAV...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Render & Mix Master", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (masterFile != null) {
                            Button(
                                onClick = { viewModel.togglePlayMaster() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ObsidianSurfaceVariant,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("play_master_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying && activePlayingId == "master_track") Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause Master",
                                    tint = CinematicGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
