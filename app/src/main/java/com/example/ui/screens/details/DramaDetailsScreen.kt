package com.example.ui.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.ui.components.EpisodeItemRow
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun DramaDetailsScreen(
    drama: Drama,
    viewModel: NoorFlixViewModel,
    onBackClick: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToVip: () -> Unit
) {
    val episodes by viewModel.currentDramaEpisodes.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isBookmarked = bookmarks.contains(drama.id)

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Episodes, 1: Details / Cast

    val posterRes = when {
        drama.id.contains("billionaire") -> R.drawable.poster_billionaire_bride
        drama.id.contains("revenge") -> R.drawable.poster_revenge_ex_wife
        drama.id.contains("heiress") -> R.drawable.poster_hidden_heiress
        else -> R.drawable.poster_billionaire_bride
    }

    Scaffold(
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("drama_details_container"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Backdrop Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    Image(
                        painter = painterResource(id = posterRes),
                        contentDescription = drama.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        Color(0x90080B14),
                                        NoorPrimaryBackground
                                    )
                                )
                            )
                    )

                    // Top Action Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("details_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleBookmark(drama.id) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .testTag("details_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.BookmarkAdded else Icons.Default.BookmarkAdd,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) NoorGold else Color.White
                                )
                            }
                            IconButton(
                                onClick = { viewModel.showMessage("Link copied to clipboard! Share with friends ✨") },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Title & Badges on bottom of backdrop
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = NoorGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = drama.genre,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NoorPrimaryBackground,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = NoorCardBackground,
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, NoorBorder)
                            ) {
                                Text(
                                    text = "${drama.totalEpisodes} Episodes",
                                    fontSize = 10.sp,
                                    color = NoorTextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = drama.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        if (drama.originalTitle.isNotBlank()) {
                            Text(
                                text = drama.originalTitle,
                                fontSize = 14.sp,
                                color = NoorGoldLight
                            )
                        }
                    }
                }
            }

            // Stats Row & Play CTA
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = NoorGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${drama.rating}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("•", color = NoorTextMuted)
                        Text("${drama.viewsCount / 1000}k Views", color = NoorTextSecondary, fontSize = 13.sp)
                        Text("•", color = NoorTextMuted)
                        Text("${drama.releaseYear}", color = NoorTextSecondary, fontSize = 13.sp)
                        Text("•", color = NoorTextMuted)
                        Text(drama.language, color = NoorGoldLight, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Play Button
                    Button(
                        onClick = {
                            val firstEp = episodes.firstOrNull()
                            if (firstEp != null) {
                                onPlayEpisode(firstEp)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NoorGold,
                            contentColor = NoorPrimaryBackground
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("details_play_first_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Play Episode 1",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Synopsis
                    Text(
                        text = drama.synopsis,
                        color = NoorTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tabs (Episodes / About)
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = NoorCardBackground,
                        contentColor = NoorGold,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = NoorGold
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Episodes (${episodes.size})", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Cast & Details", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Tab Content
            if (selectedTab == 0) {
                items(episodes) { ep ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        EpisodeItemRow(
                            episode = ep,
                            isCurrent = false,
                            isVipUser = user.isVip,
                            onClick = { onPlayEpisode(ep) }
                        )
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(NoorCardBackground, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text("Director", color = NoorTextMuted, fontSize = 12.sp)
                        Text(drama.director, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Starring Cast", color = NoorTextMuted, fontSize = 12.sp)
                        Text(drama.cast.joinToString(", "), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Keywords & Tags", color = NoorTextMuted, fontSize = 12.sp)
                        Text(drama.tags.joinToString(" • "), color = NoorGoldLight, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Production & Rights", color = NoorTextMuted, fontSize = 12.sp)
                        Text("© NoorFlix Originals. All rights reserved. 4K Ultra HD Streaming.", color = NoorTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
