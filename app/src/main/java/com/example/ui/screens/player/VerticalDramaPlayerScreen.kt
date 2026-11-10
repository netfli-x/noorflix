package com.example.ui.screens.player

import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.AccessType
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.ui.components.CoinsBadge
import com.example.ui.components.EpisodeItemRow
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerticalDramaPlayerScreen(
    viewModel: NoorFlixViewModel,
    onBackClick: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToVip: () -> Unit
) {
    val context = LocalContext.current
    val drama by viewModel.selectedDrama.collectAsState()
    val episode by viewModel.selectedEpisode.collectAsState()
    val episodes by viewModel.currentDramaEpisodes.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val countdown by viewModel.autoPlayCountdown.collectAsState()

    val isBookmarked = drama != null && bookmarks.contains(drama!!.id)

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(1L) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var showEpisodesDrawer by remember { mutableStateOf(false) }
    var showSpeedModal by remember { mutableStateOf(false) }
    var currentSpeed by remember { mutableFloatStateOf(1.0f) }
    var isLiked by remember { mutableStateOf(false) }

    val currentEp = episode
    val isEpisodeLocked = currentEp != null &&
            !currentEp.isUnlocked &&
            !user.isVip &&
            currentEp.accessType != AccessType.FREE

    // Setup ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Load media when episode changes
    LaunchedEffect(currentEp?.id, isEpisodeLocked) {
        if (currentEp != null && !isEpisodeLocked) {
            val mediaItem = MediaItem.fromUri(currentEp.videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            isPlaying = true
        } else {
            exoPlayer.pause()
            isPlaying = false
        }
    }

    // Track playback listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    totalDurationMs = exoPlayer.duration.coerceAtLeast(1L)
                } else if (state == Player.STATE_ENDED) {
                    // Trigger Auto-Play Next Countdown
                    viewModel.startAutoPlayNextCountdown {
                        val hasNext = viewModel.playNextEpisode()
                        if (!hasNext) {
                            viewModel.showMessage("You've reached the last episode of this drama!")
                        }
                    }
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // Periodic time update
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPositionMs = exoPlayer.currentPosition
            totalDurationMs = exoPlayer.duration.coerceAtLeast(1L)
            viewModel.updateWatchProgress(currentPositionMs, totalDurationMs)
            delay(500)
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(areControlsVisible) {
        if (areControlsVisible) {
            delay(4000)
            areControlsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
                        if (totalDrag < -60f) {
                            // Swiped Up -> Next Episode
                            val nextAvailable = viewModel.playNextEpisode()
                            if (nextAvailable) {
                                viewModel.showMessage("Playing Next Episode")
                            }
                        } else if (totalDrag > 60f) {
                            // Swiped Down -> Previous Episode
                            val prevAvailable = viewModel.playPreviousEpisode()
                            if (prevAvailable) {
                                viewModel.showMessage("Playing Previous Episode")
                            }
                        } else {
                            // Tap toggle
                            areControlsVisible = !areControlsVisible
                        }
                    }
                )
            }
            .testTag("vertical_player_container")
    ) {
        // ExoPlayer View
        if (!isEpisodeLocked) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gradient Overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
        )

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("player_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
            ) {
                Text(
                    text = drama?.title ?: "NoorFlix Drama",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Text(
                    text = "Episode ${currentEp?.episodeNumber ?: 1}: ${currentEp?.title ?: ""}",
                    color = NoorGoldLight,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            CoinsBadge(
                coins = user.coins,
                onClick = onNavigateToWallet
            )
        }

        // Right-Side Interactive Sidebar (TikTok / ReelShort style)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Button
            IconButton(
                onClick = { isLiked = !isLiked },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("player_like_button")
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) NoorCoral else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // My List Bookmark
            IconButton(
                onClick = { drama?.let { viewModel.toggleBookmark(it.id) } },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("player_bookmark_button")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.BookmarkAdded else Icons.Default.BookmarkAdd,
                    contentDescription = "Save to My List",
                    tint = if (isBookmarked) NoorGold else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Episodes Drawer Button
            IconButton(
                onClick = { showEpisodesDrawer = true },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("player_episodes_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = "Episode List",
                    tint = NoorGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Speed Selector
            IconButton(
                onClick = { showSpeedModal = true },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "${currentSpeed}x",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Big Play/Pause overlay when controls visible
        AnimatedVisibility(
            visible = areControlsVisible && !isEpisodeLocked,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                    isPlaying = !isPlaying
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .testTag("player_center_play_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = NoorGold,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Bottom Controls (Seekbar + Prev / Next)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Seekbar
            if (!isEpisodeLocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progressFraction = if (totalDurationMs > 0) {
                        (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Slider(
                        value = progressFraction,
                        onValueChange = { frac ->
                            val seekPos = (frac * totalDurationMs).toLong()
                            exoPlayer.seekTo(seekPos)
                            currentPositionMs = seekPos
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = NoorGold,
                            activeTrackColor = NoorGold,
                            inactiveTrackColor = NoorBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val currSec = (currentPositionMs / 1000).toInt()
                    val totalSec = (totalDurationMs / 1000).toInt()
                    Text(
                        text = String.format("%02d:%02d / %02d:%02d", currSec / 60, currSec % 60, totalSec / 60, totalSec % 60),
                        color = NoorTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.playPreviousEpisode() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev Ep", fontSize = 12.sp)
                }

                Text(
                    text = "Swipe ↑ for next episode",
                    color = NoorTextMuted,
                    fontSize = 11.sp
                )

                TextButton(
                    onClick = { viewModel.playNextEpisode() },
                    colors = ButtonDefaults.textButtonColors(contentColor = NoorGold)
                ) {
                    Text("Next Ep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next")
                }
            }
        }

        // Auto-Play Next Countdown Banner
        countdown?.let { count ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoorGold),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Next Episode Playing In...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$count",
                        color = NoorGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 42.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.cancelAutoPlay() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                viewModel.cancelAutoPlay()
                                viewModel.playNextEpisode()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground)
                        ) {
                            Text("Play Now")
                        }
                    }
                }
            }
        }

        // Locked Episode Modal Overlay
        if (isEpisodeLocked && currentEp != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NoorPrimaryBackground.copy(alpha = 0.94f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorGoldDark),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                        .testTag("locked_episode_modal")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(NoorGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentEp.accessType == AccessType.VIP_ONLY) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = NoorGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (currentEp.accessType == AccessType.VIP_ONLY) "VIP Exclusive Episode" else "Locked Episode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (currentEp.accessType == AccessType.VIP_ONLY)
                                "This episode is exclusive to NoorFlix VIP members."
                            else
                                "Unlock Episode ${currentEp.episodeNumber} with ${currentEp.coinPrice} Coins to continue watching.",
                            color = NoorTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Your Balance:", color = NoorTextMuted, fontSize = 13.sp)
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = NoorGold, modifier = Modifier.size(16.dp))
                            Text("${user.coins} Coins", color = NoorGoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (currentEp.accessType == AccessType.VIP_ONLY) {
                            Button(
                                onClick = onNavigateToVip,
                                colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text("Upgrade to VIP", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            if (user.coins >= currentEp.coinPrice) {
                                Button(
                                    onClick = {
                                        viewModel.unlockEpisode(currentEp) {
                                            // on success
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("unlock_with_coins_button")
                                ) {
                                    Text("Unlock with ${currentEp.coinPrice} Coins", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = onNavigateToWallet,
                                    colors = ButtonDefaults.buttonColors(containerColor = NoorCoral, contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("recharge_coins_button")
                                ) {
                                    Text("Recharge Coins", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Episodes Bottom Sheet Drawer
        if (showEpisodesDrawer) {
            ModalBottomSheet(
                onDismissRequest = { showEpisodesDrawer = false },
                containerColor = NoorSecondaryBackground,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Episodes (${episodes.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { showEpisodesDrawer = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 420.dp)
                    ) {
                        items(episodes) { ep ->
                            EpisodeItemRow(
                                episode = ep,
                                isCurrent = ep.id == currentEp?.id,
                                isVipUser = user.isVip,
                                onClick = {
                                    viewModel.selectEpisode(ep)
                                    showEpisodesDrawer = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Speed Selection Modal
        if (showSpeedModal) {
            AlertDialog(
                onDismissRequest = { showSpeedModal = false },
                title = { Text("Playback Speed", color = Color.White, fontWeight = FontWeight.Bold) },
                containerColor = NoorElevatedSurface,
                text = {
                    Column {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentSpeed == speed) NoorGold.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        currentSpeed = speed
                                        exoPlayer.setPlaybackSpeed(speed)
                                        viewModel.playbackSpeed.value = speed
                                        showSpeedModal = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${speed}x", color = if (currentSpeed == speed) NoorGold else Color.White, fontWeight = FontWeight.SemiBold)
                                if (currentSpeed == speed) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = NoorGold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedModal = false }) {
                        Text("Close", color = NoorGold)
                    }
                }
            )
        }
    }
}
