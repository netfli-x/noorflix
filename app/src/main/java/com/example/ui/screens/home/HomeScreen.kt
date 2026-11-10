package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Drama
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun HomeScreen(
    viewModel: NoorFlixViewModel,
    onDramaClick: (Drama) -> Unit,
    onPlayDrama: (Drama) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToVip: () -> Unit,
    onSeeAllCategory: (String) -> Unit
) {
    val allDramas by viewModel.allDramas.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val cmsConfig by viewModel.cmsConfig.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val unreadNotifications = notifications.count { !it.isRead }

    // Featured hero drama
    val heroDrama = allDramas.firstOrNull { it.id == cmsConfig.heroDramaId }
        ?: allDramas.firstOrNull { it.isFeatured }
        ?: allDramas.firstOrNull()

    val trendingDramas = allDramas.filter { it.isTrending }
    val originals = allDramas.filter { it.isExclusive }
    val billionaireDramas = allDramas.filter { it.genre.contains("Billionaire", ignoreCase = true) || it.genre.contains("CEO", ignoreCase = true) }
    val revengeDramas = allDramas.filter { it.genre.contains("Revenge", ignoreCase = true) || it.genre.contains("Suspense", ignoreCase = true) }
    val vipDramas = allDramas.filter { it.isVipOnly }
    val topRated = allDramas.sortedByDescending { it.rating }

    // Continue watching dramas
    val continueWatchingDramas = remember(watchHistory, allDramas) {
        watchHistory.mapNotNull { hist ->
            allDramas.firstOrNull { it.id == hist.dramaId }
        }.distinctBy { it.id }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(NoorPrimaryBackground)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NoorFlixLogo()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoinsBadge(
                        coins = user.coins,
                        onClick = onNavigateToWallet
                    )

                    VipBadge(
                        isVip = user.isVip,
                        onClick = onNavigateToVip
                    )

                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.size(36.dp).testTag("home_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }

                    Box {
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier.size(36.dp).testTag("home_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                        if (unreadNotifications > 0) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp, end = 4.dp),
                                containerColor = NoorCoral
                            ) {
                                Text("$unreadNotifications", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        },
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_scroll_container"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Hero Banner
            item {
                if (heroDrama != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        HeroBanner(
                            drama = heroDrama,
                            onPlayClick = { onPlayDrama(heroDrama) },
                            onDetailsClick = { onDramaClick(heroDrama) },
                            onListToggle = { viewModel.toggleBookmark(heroDrama.id) },
                            isBookmarked = bookmarks.contains(heroDrama.id)
                        )
                    }
                }
            }

            // Continue Watching Row (if any)
            if (continueWatchingDramas.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Continue Watching",
                        onSeeAllClick = null
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(continueWatchingDramas) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onPlayDrama(drama) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Trending Now
            if (cmsConfig.showTrendingRow && trendingDramas.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Trending Now 🔥",
                        onSeeAllClick = { onSeeAllCategory("Trending") }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(trendingDramas) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Exclusive NoorFlix Originals
            if (cmsConfig.showOriginalsRow && originals.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Exclusive Originals ⭐",
                        onSeeAllClick = { onSeeAllCategory("Originals") }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(originals) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Billionaire & CEO Romance
            if (cmsConfig.showBillionaireRow && billionaireDramas.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Billionaire & CEO Romance 💎",
                        onSeeAllClick = { onSeeAllCategory("Billionaire") }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(billionaireDramas) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Revenge & Drama
            if (cmsConfig.showRevengeRow && revengeDramas.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Revenge & Retribution ⚡",
                        onSeeAllClick = { onSeeAllCategory("Revenge") }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(revengeDramas) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Top Rated
            if (topRated.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Top Rated Series 🏆",
                        onSeeAllClick = { onSeeAllCategory("Top Rated") }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(topRated) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // VIP Exclusive Dramas
            if (cmsConfig.showVipRow && vipDramas.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "VIP Members Only 👑",
                        onSeeAllClick = { onNavigateToVip() }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vipDramas) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) }
                            )
                        }
                    }
                }
            }
        }
    }
}
