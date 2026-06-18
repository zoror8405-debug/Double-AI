package com.example.view

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.AnimeDubViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDubStudioScreen(
    viewModel: AnimeDubViewModel,
    modifier: Modifier = Modifier
) {
    val selectedProject by viewModel.selectedProject.collectAsState()
    val allProjects by viewModel.projects.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val activeSubtitleCue by viewModel.activeSubtitle.collectAsState()
    val selectedCharacterId by viewModel.selectedCharacterId.collectAsState()
    val vocalFrequencies by viewModel.vocalFrequencies.collectAsState()
    val lipSyncConfig by viewModel.lipSyncConfig.collectAsState()
    val studioLogs by viewModel.studioLogs.collectAsState()
    val isTranslatingWithGemini by viewModel.isTranslatingWithGemini.collectAsState()

    // Recording & Compilation status
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingProgress by viewModel.recordingProgress.collectAsState()
    val voiceCloneStatus by viewModel.voiceCloneStatus.collectAsState()
    val isGeneratingMaster by viewModel.isGeneratingMaster.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val masterSuccessMsg by viewModel.masterSuccessMsg.collectAsState()

    // Upload & Download Custom Video states
    val isUploadingVideo by viewModel.isUploadingVideo.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val isDownloadingVideo by viewModel.isDownloadingVideo.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val showUploadDialog by viewModel.showUploadDialog.collectAsState()

    // Active character object
    val activeVoiceProfile = selectedProject.characters.firstOrNull { it.id == selectedCharacterId }
        ?: selectedProject.characters.firstOrNull()

    // Full layout wraps in dark immersive style
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. CINEMATIC APP HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1C1E))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo symbol with beautiful gradient matches HTML theme
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFD0BCFF), Color(0xFF381E72))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = Color(0xFF1D1B20),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "DubSync AI Studio",
                            color = Color(0xFFE2E2E6),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "অ্যানিমে ডাবিং ও নিখুঁত লিপ-সিঙ্ক টুল",
                            color = Color(0xFF9E9E9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                // Batch translate helper
                IconButton(
                    onClick = { viewModel.translateAllSubtitlesWithGemini() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF2F3033)),
                    enabled = !isTranslatingWithGemini
                ) {
                    if (isTranslatingWithGemini) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFFD0BCFF),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Batch Translate entire script",
                            tint = Color(0xFFD0BCFF)
                        )
                    }
                }
            }

            // 2. MAIN WORKSPACE SCROLLABLE TIMELINE EDITOR
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // TAB 1: SELECT ACTIVE MOVIE PROJECT
                item {
                    Text(
                        text = "Active Anime Movies (সিনেমা নির্বাচন করুন)",
                        color = Color(0xFFC6C6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(115.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF131315))
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF00FF66).copy(alpha = 0.6f), Color(0xFF00E5FF).copy(alpha = 0.6f))
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.setShowUploadDialog(true) }
                                    .padding(12.dp)
                                    .testTag("upload_video_trigger")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload Video",
                                        tint = Color(0xFF00FF66),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Upload custom Video",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "নিজের ভিডিও আপলোড করুন",
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 8.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        items(allProjects) { proj ->
                            val isSelected = proj.id == selectedProject.id
                            val borderBrush = if (isSelected) {
                                Brush.linearGradient(colors = listOf(Color(0xFFD0BCFF), Color(0xFF00E5FF)))
                            } else {
                                Brush.linearGradient(colors = listOf(Color(0xFF2F3033), Color(0xFF2F3033)))
                            }

                            Box(
                                modifier = Modifier
                                    .width(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1A1C1E))
                                    .border(2.dp, borderBrush, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.selectProject(proj) }
                                    .padding(12.dp)
                                    .testTag("movie_card_${proj.id}")
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x30D0BCFF))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = proj.genre,
                                                color = Color(0xFFD0BCFF),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = Color(0xFF00E5FF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = proj.title,
                                        color = Color(0xFFE2E2E6),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = proj.bhashaDescription,
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 9.sp,
                                        lineHeight = 11.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 2: CINEMATIC VISUAL LIP-SYNC MONITOR
                item {
                    Text(
                        text = "Real-time Lip-Sync Monitor (লাইভ লিপ-সিঙ্ক প্রিভিউ)",
                        color = Color(0xFFC6C6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF050507)),
                        border = BorderStroke(1.dp, Color(0xFF2F3033)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Aspect Ratio 1.8 (approx video size) Lip Mesh Animator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.77f)
                            ) {
                                LipSyncCanvasPlayer(
                                    characterName = activeSubtitleCue?.characterName ?: (activeVoiceProfile?.name ?: "Unknown"),
                                    emotion = activeSubtitleCue?.emotion ?: VoiceEmotion.NEUTRAL,
                                    playbackTimeMs = currentTimeMs,
                                    activeSubtitleCue = activeSubtitleCue,
                                    isAudioPlaying = isPlaying,
                                    vocalFrequencies = vocalFrequencies
                                )

                                // Live status bar tag overlay
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xCC111115))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isPlaying) Color.Green else Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TARGET DUB: ${targetLanguage.displayName.uppercase()}",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Quick precision accuracy badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xE6000000))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Acc: ${(lipSyncConfig.accuracyMultiplier * 100)}%",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Active progress transport timeline
                            Column(modifier = Modifier.padding(16.dp)) {
                                val totalDurationSeconds = selectedProject.durationSeconds
                                val totalDurationMs = totalDurationSeconds * 1000f

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = viewModel.currentTimeFormatted,
                                        color = Color(0xFFD0BCFF),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Render a digital audio waveform bar inside the tracker!
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        vocalFrequencies.forEach { level ->
                                            Box(
                                                modifier = Modifier
                                                    .width(2.5.dp)
                                                    .height((level * 24).dp.coerceAtLeast(3.dp))
                                                    .clip(RoundedCornerShape(1.dp))
                                                    .background(Color(0xFF00E5FF))
                                            )
                                        }
                                    }

                                    Text(
                                        text = String.format("%02d:%02d.00", totalDurationSeconds / 60, totalDurationSeconds % 60),
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Slider(
                                    value = currentTimeMs.toFloat(),
                                    onValueChange = { viewModel.seekTo(it.toLong()) },
                                    valueRange = 0f..totalDurationMs,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFFD0BCFF),
                                        activeTrackColor = Color(0xFFD0BCFF),
                                        inactiveTrackColor = Color(0xFF2F3033)
                                    ),
                                    modifier = Modifier.testTag("timeline_slider")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Media Controls Center Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { viewModel.seekTo(0) },
                                        enabled = true
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SkipPrevious,
                                            contentDescription = "Skip back",
                                            tint = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    FilledIconButton(
                                        onClick = { viewModel.togglePlayback() },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Color(0xFFD0BCFF),
                                            contentColor = Color(0xFF381E72)
                                        ),
                                        modifier = Modifier
                                            .size(54.dp)
                                            .testTag("play_pause_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play preview override",
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    IconButton(
                                        onClick = {
                                            val finalMs = (selectedProject.durationSeconds * 1000 - 100).toLong()
                                            viewModel.seekTo(finalMs)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SkipNext,
                                            contentDescription = "Skip forward",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: TARGET LANGUAGE SELECTION TABS
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Target Dub Language (ডাবিংয়ের লক্ষ্য ভাষা)",
                            color = Color(0xFFC6C6CA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1A1C1E))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DubLanguage.values().forEach { lang ->
                                val selected = targetLanguage == lang
                                val bg = if (selected) Color(0xFFD0BCFF) else Color.Transparent
                                val tc = if (selected) Color(0xFF381E72) else Color(0xFFE2E2E6)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bg)
                                        .clickable { viewModel.setTargetLanguage(lang) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang.bhasha,
                                        color = tc,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 4: SCRIPTS & ACTIVE SUBTITLE STUDIO CUES
                item {
                    Text(
                        text = "Syllable Lipsync Script Timeline (সংলাপ ও উচ্চারণ সংশোধন)",
                        color = Color(0xFFC6C6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                items(selectedProject.subtitles) { cue ->
                    val isActiveCue = activeSubtitleCue?.id == cue.id
                    val cardBg = if (isActiveCue) Color(0xFF1E1C24) else Color(0xFF1A1C1E)
                    val outBorder = if (isActiveCue) {
                        BorderStroke(1.5.dp, Color(0xFFD0BCFF))
                    } else {
                        BorderStroke(1.dp, Color(0xFF222326))
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = outBorder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.seekTo(cue.startTimeMs) }
                            .padding(vertical = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Subtitle cue descriptor header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Colored profile bubble
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E5FF))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${cue.characterName}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "${String.format("%.1f", cue.startTimeMs / 1000f)}s - ${String.format("%.1f", cue.endTimeMs / 1000f)}s",
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Original dialogue
                            Text(
                                text = "Original: ${cue.originalText}",
                                color = Color(0xFF9E9E9E),
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Assistant tag and status check badge
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val speakerProfile = selectedProject.characters.firstOrNull { 
                                    it.name.contains(cue.characterName, ignoreCase = true) || cue.characterName.contains(it.name, ignoreCase = true) 
                                }
                                val role = speakerProfile?.assistantRole ?: "অনুবাদ সহকারী"
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = null,
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Assigned Assistant: $role",
                                        color = Color(0xFFD0BCFF),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (cue.getTextForLanguage(targetLanguage).isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF66),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Dub Ready",
                                            color = Color(0xFF00FF66),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Editable translation input container
                            val currentTranslateText = cue.getTextForLanguage(targetLanguage)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F1113))
                                    .border(1.dp, Color(0xFF2F3033), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                BasicTextField(
                                    value = currentTranslateText,
                                    onValueChange = { newVal ->
                                        viewModel.updateSubTranslationText(cue.id, targetLanguage, newVal)
                                    },
                                    textStyle = TextStyle(
                                        color = Color(0xFFE2E2E6),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Button invoking Gemini translation with assistant parameters
                                Button(
                                    onClick = { viewModel.translateSubtitleWithGemini(cue.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00E5FF),
                                        contentColor = Color(0xFF131313)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = Color(0xFF131313)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Assistant Dub",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Emotion Adjustment parameters for Real Life expressions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Expression Tone: ",
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(cue.emotion.colorHex).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = cue.emotion.displayName,
                                            color = Color(cue.emotion.colorHex),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Quick emotions switch bar
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    VoiceEmotion.values().forEach { emotionOption ->
                                        val isSelectedEmotion = cue.emotion == emotionOption
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelectedEmotion) Color(
                                                        emotionOption.colorHex
                                                    ) else Color(0xFF2F3033)
                                                )
                                                .clickable {
                                                    viewModel.updateSubtitleEmotion(
                                                        cue.id,
                                                        emotionOption
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Tiny letter icon
                                            Text(
                                                text = emotionOption.displayName.take(1),
                                                color = if (isSelectedEmotion) Color.White else Color(0xFF9E9E9E),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 5: VOICE CLONING & SEPARATE DIALOG PERSONAS
                item {
                    Text(
                        text = "Separate Character Voices & Mic Cloning (ভয়েস প্রফাইল ও ক্লোনিং)",
                        color = Color(0xFFC6C6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1B20)),
                        border = BorderStroke(1.dp, Color(0xFF49454F)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Select Character profile to adjust natural realistic tone parameters:",
                                color = Color(0xFFC6C6CA),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Character select row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedProject.characters.forEach { profile ->
                                    val isSelected = profile.id == selectedCharacterId
                                    val bg = if (isSelected) Color(0xFF381E72) else Color(0xFF2F3033)
                                    val tc = if (isSelected) Color(0xFFD0BCFF) else Color(0xFFE2E2E6)

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(bg)
                                            .clickable { viewModel.setSelectedCharacter(profile.id) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(profile.color)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = profile.name.substringBefore(" "),
                                            color = tc,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (profile.isCloned) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = "Cloned",
                                                tint = Color(0xFF00E5FF),
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Sliders for dynamic humanness tuning (Pitch, Resonance, Warmth)
                            activeVoiceProfile?.let { profile ->
                                Text(
                                    text = "Editing Voice: ${profile.name}",
                                    color = Color(0xFFD0BCFF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Pitch modifier
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Base Pitch", color = Color(0xFF9E9E9E), fontSize = 11.sp, modifier = Modifier.width(80.dp))
                                    Slider(
                                        value = profile.basePitch,
                                        onValueChange = { newVal ->
                                            viewModel.updateCharacterVoiceParams(
                                                profile.id,
                                                newVal,
                                                profile.resonance,
                                                profile.warmth
                                            )
                                        },
                                        valueRange = 0.5f..2.0f,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${String.format("%.1f", profile.basePitch)}x",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.width(30.dp),
                                        textAlign = TextAlign.End
                                    )
                                }

                                // Resonance modifier
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Chest Deep", color = Color(0xFF9E9E9E), fontSize = 11.sp, modifier = Modifier.width(80.dp))
                                    Slider(
                                        value = profile.resonance,
                                        onValueChange = { newVal ->
                                            viewModel.updateCharacterVoiceParams(
                                                profile.id,
                                                profile.basePitch,
                                                newVal,
                                                profile.warmth
                                            )
                                        },
                                        valueRange = 0f..1f,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${(profile.resonance * 100).toInt()}%",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.width(30.dp),
                                        textAlign = TextAlign.End
                                    )
                                }

                                // Warmth human factor
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Human Warmth", color = Color(0xFF9E9E9E), fontSize = 11.sp, modifier = Modifier.width(80.dp))
                                    Slider(
                                        value = profile.warmth,
                                        onValueChange = { newVal ->
                                            viewModel.updateCharacterVoiceParams(
                                                profile.id,
                                                profile.basePitch,
                                                profile.resonance,
                                                newVal
                                            )
                                        },
                                        valueRange = 0.5f..1f,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${(profile.warmth * 100).toInt()}%",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.width(30.dp),
                                        textAlign = TextAlign.End
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Dynamic voice cloning interaction with microphone simulation
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F1113))
                                        .border(1.dp, Color(0xFF2F3033), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledIconButton(
                                        onClick = { viewModel.startVoiceCloning() },
                                        enabled = !isRecording,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Color(0xFF00E5FF),
                                            contentColor = Color(0xFF131313)
                                        ),
                                        modifier = Modifier.testTag("clone_mic_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Clone Actor Voice Microphone")
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isRecording) "Recording Voice Cloner..." else "Actor Clone System",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = voiceCloneStatus,
                                            color = Color(0xFF9E9E9E),
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp
                                        )

                                        if (isRecording) {
                                            LinearProgressIndicator(
                                                progress = { recordingProgress },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 6.dp),
                                                color = Color(0xFF00E5FF),
                                                trackColor = Color(0xFF2F3033)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFF49454F).copy(alpha = 0.5f), thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = null,
                                        tint = Color(0xFF00FF66),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "🤖 ${profile.name}-এর ডাবিং ও লিপ-সিঙ্ক সহকারী হাব",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1113)),
                                    border = BorderStroke(1.dp, Color(0xFF2F3033)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "ডিজিটাল সহকারীর স্ট্যাটাস:",
                                                color = Color(0xFF9E9E9E),
                                                fontSize = 11.sp
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF00FF66))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = profile.assistantStatus,
                                                    color = Color(0xFF00FF66),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "সহকারীর পরিচয় (Assistant Designation / Role)",
                                            color = Color(0xFFD0BCFF),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        var localRole by remember(profile.id) { mutableStateOf(profile.assistantRole) }
                                        BasicTextField(
                                            value = localRole,
                                            onValueChange = { localRole = it },
                                            textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(Color(0x20FFFFFF), RoundedCornerShape(6.dp))
                                                .padding(8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "অনুবাদ ও ডাবিং নির্দেশিকা (Assistant Instructions / prompt)",
                                            color = Color(0xFFD0BCFF),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        var localInstructions by remember(profile.id) { mutableStateOf(profile.assistantInstructions) }
                                        BasicTextField(
                                            value = localInstructions,
                                            onValueChange = { localInstructions = it },
                                            textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(Color(0x20FFFFFF), RoundedCornerShape(6.dp))
                                                .padding(8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = {
                                                viewModel.updateCharacterAssistantInstructions(
                                                    profile.id,
                                                    localRole,
                                                    localInstructions
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFD0BCFF),
                                                contentColor = Color(0xFF381E72)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("সহকারী নির্দেশিকা আপডেট করুন (Save Assistant Settings)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 6: ADVANCED DYNAMIC LIP-SYNCH ADJUSTMENT SETTINGS
                item {
                    Text(
                        text = "Real-time Lip Warp Calibration (ঠোঁটের মুভমেন্ট ক্যালিব্রেশন)",
                        color = Color(0xFFC6C6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1B20)),
                        border = BorderStroke(1.dp, Color(0xFF2F3033))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Lip warp accuracy slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Accuracy Multiplier",
                                    color = Color(0xFFC6C6CA),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${(lipSyncConfig.accuracyMultiplier * 100).toInt()}%",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = lipSyncConfig.accuracyMultiplier,
                                onValueChange = { viewModel.updateLipSyncConfig(lipSyncConfig.copy(accuracyMultiplier = it)) },
                                valueRange = 0.8f..1.0f
                            )

                            // Facial Mesh Sensitivity
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Mesh Sensitivity",
                                    color = Color(0xFFC6C6CA),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${(lipSyncConfig.facialMeshSensitivity * 100).toInt()}%",
                                    color = Color(0xFFD0BCFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = lipSyncConfig.facialMeshSensitivity,
                                onValueChange = { viewModel.updateLipSyncConfig(lipSyncConfig.copy(facialMeshSensitivity = it)) },
                                valueRange = 0.5f..1.0f
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Biological features checkboxes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = lipSyncConfig.autoBreathPauses,
                                        onCheckedChange = { viewModel.updateLipSyncConfig(lipSyncConfig.copy(autoBreathPauses = it)) },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD0BCFF))
                                    )
                                    Text(
                                        text = "Biological micro-breath pauses",
                                        color = Color(0xFFE2E2E6),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 7: TERMINAL RUNTIME WORK LOGS
                item {
                    Text(
                        text = "Neural Compiling Pipeline Logs (সিস্টেম রানিং লগ)",
                        color = Color(0xFFC6C6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF070809))
                            .border(1.dp, Color(0xFF1A1C1D), RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(
                            reverseLayout = true,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(studioLogs.asReversed()) { logMsg ->
                                Text(
                                    text = logMsg,
                                    color = Color(0xFF00FF66),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // TAB 8: MASTER PIPELINE TRIGGER
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.triggerMasterDubCompilation() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD0BCFF),
                            contentColor = Color(0xFF381E72)
                        ),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("generate_master_dub_button"),
                        enabled = !isGeneratingMaster
                    ) {
                        if (isGeneratingMaster) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF381E72)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Muxing Lip-Sync Frame Mux: ${(generationProgress * 100).toInt()}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Generate AI Master Dub (ডাবিং রেন্ডার করুন)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 3. SECURE IMMERSIVE LOWER NAVIGATOR
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = Color(0xFF2F3033), thickness = 1.dp)
                NavigationBar(
                    containerColor = Color(0xFF1A1C1E)
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(imageVector = Icons.Rounded.Movie, contentDescription = "Active studio projects", tint = Color(0xFFD0BCFF)) },
                        label = { Text("Projects", fontSize = 10.sp, color = Color(0xFFD0BCFF)) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.triggerMasterDubCompilation() },
                        icon = { Icon(imageVector = Icons.Rounded.DownloadDone, contentDescription = "Finished compilations") },
                        label = { Text("Export") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(imageVector = Icons.Rounded.Help, contentDescription = "Learn how to lip sync") },
                        label = { Text("Support") }
                    )
                }
            }
        }

        // 4. FLOATING IMMERSIVE MODAL DIALOG ON SUCCESS DIRECT EXPORT
        masterSuccessMsg?.let { successText ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissSuccessDialog() },
                confirmButton = {
                    Button(
                        onClick = { viewModel.dismissSuccessDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF), contentColor = Color(0xFF381E72))
                    ) {
                        Text("ব্রাভো! (Bravo)")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00FF66))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dub Compilation Success!",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = successText,
                            color = Color(0xFFC6C6CA),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Export details:\n• Format: Ultra HD H.264 mp4\n• Bengali dynamic mouth offset: 0ms (perfect real-world human simulation)\n• Syllables alignment: 99.8%",
                            color = Color(0xFF9E9E9E),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFF2F3033), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        if (isDownloadingVideo) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Downloading custom MP4 cluster...",
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "${(downloadProgress * 100).toInt()}%",
                                        color = Color(0xFF00FF66),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { downloadProgress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF00FF66),
                                    trackColor = Color(0xFF2F3033)
                                )
                            }
                        } else {
                            if (downloadProgress >= 1f) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF00FF66).copy(alpha = 0.15f))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF00FF66),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "HQ MP4 Video successfully downloaded to local Downloads storage!",
                                        color = Color(0xFF00FF66),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.downloadDubbedVideo() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00FF66),
                                        contentColor = Color(0xFF131313)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .testTag("download_dub_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DownloadForOffline,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ডাবড ভিডিও ডাউনলোড (Download MP4)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = Color(0xFF1D1B20),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.padding(16.dp)
            )
        }

        // 5. FLOATING DIALOG FOR UPLOADING CUSTOM VIDEOS
        if (showUploadDialog) {
            var inputTitle by remember { mutableStateOf("My Custom Anime Clip") }
            var originalLang by remember { mutableStateOf("Japanese") }
            var inputDuration by remember { mutableStateOf(25f) }
            var inputCharacters by remember { mutableStateOf("Tanjiro, Akaza, Rengoku") }

            AlertDialog(
                onDismissRequest = { if (!isUploadingVideo) viewModel.setShowUploadDialog(false) },
                confirmButton = {
                    if (isUploadingVideo) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF00FF66)
                        )
                    } else {
                        Button(
                            onClick = {
                                viewModel.uploadCustomVideo(
                                    title = inputTitle,
                                    originalLanguage = originalLang,
                                    durationSeconds = inputDuration.toInt(),
                                    charactersInput = inputCharacters
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF66),
                                contentColor = Color(0xFF131313)
                            )
                        ) {
                            Text("শুরু করুন (Start Processing)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                },
                dismissButton = {
                    if (!isUploadingVideo) {
                        TextButton(onClick = { viewModel.setShowUploadDialog(false) }) {
                            Text("বাতিল (Cancel)", color = Color(0xFF9E9E9E))
                        }
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF00FF66))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Raw Video File", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "নিজের যেকোনো ভাষার ভিডিও আপলোড করুন। এআই সিস্টেমে ভিডিওটি ডিকোড করে ক্যারেক্টার আইডেন্টিফাই করা হবে এবং প্রত্যেকের জন্য একটি নিজস্ব সহকারী তৈরি হবে।",
                            color = Color(0xFFC6C6CA),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        if (isUploadingVideo) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                LinearProgressIndicator(
                                    progress = { uploadProgress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF00FF66),
                                    trackColor = Color(0xFF2F3033)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Analyzing facial structure & actors: ${(uploadProgress * 100).toInt()}%",
                                    color = Color(0xFF00FF66),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            // Fields
                            Text(
                                text = "Video Title (ভিডিওর শিরোনাম)",
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            BasicTextField(
                                value = inputTitle,
                                onValueChange = { inputTitle = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F1113), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF2F3033), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            )

                            Text(
                                text = "Source Language (ভিডিওর মূল ভাষা)",
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            BasicTextField(
                                value = originalLang,
                                onValueChange = { originalLang = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F1113), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF2F3033), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            )

                            Text(
                                text = "Dialogue Characters ( actors names comma separated)",
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            BasicTextField(
                                value = inputCharacters,
                                onValueChange = { inputCharacters = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F1113), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF2F3033), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            )
                            Text(
                                text = "💡 কমা দিয়ে আলাদা করুন যেমন: Tanjiro, Akaza, Rengoku",
                                color = Color(0xFF9E9E9E),
                                fontSize = 9.sp
                            )

                            Text(
                                text = "Approx Duration: ${inputDuration.toInt()} seconds",
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = inputDuration,
                                onValueChange = { inputDuration = it },
                                valueRange = 10f..60f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF00FF66),
                                    activeTrackColor = Color(0xFF00FF66),
                                    inactiveTrackColor = Color(0xFF2F3033)
                                )
                            )
                        }
                    }
                },
                containerColor = Color(0xFF1D1B20),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

// Simple custom BasicTextField using basic Compose wrapping to prevent complex custom text implementations
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier.fillMaxWidth(),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD0BCFF))
    )
}
