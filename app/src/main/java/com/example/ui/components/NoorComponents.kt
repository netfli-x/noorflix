package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AccessType
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.ui.theme.*

@Composable
fun NoorFlixLogo(
    modifier: Modifier = Modifier,
    size: Int = 36
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(NoorGold, NoorCardBackground),
                        radius = 80f
                    )
                )
                .border(1.dp, NoorGoldDark, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_noorflix_logo),
                contentDescription = "NoorFlix Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NOOR",
                    fontWeight = FontWeight.Black,
                    fontSize = (size * 0.52).sp,
                    letterSpacing = 1.5.sp,
                    color = NoorGold
                )
                Text(
                    text = "FLIX",
                    fontWeight = FontWeight.Light,
                    fontSize = (size * 0.52).sp,
                    letterSpacing = 2.sp,
                    color = NoorTextPrimary
                )
            }
        }
    }
}

@Composable
fun CoinsBadge(
    coins: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = NoorCardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, NoorGoldDark),
        modifier = modifier.testTag("coins_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Coins",
                tint = NoorGold,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$coins",
                color = NoorGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add Coins",
                tint = NoorGold,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun VipBadge(
    isVip: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isVip) NoorGoldDark.copy(alpha = 0.3f) else NoorCardBackground,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isVip) NoorGold else NoorBorder
        ),
        modifier = modifier.testTag("vip_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "VIP Status",
                tint = if (isVip) NoorGold else NoorTextMuted,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isVip) "VIP" else "GET VIP",
                color = if (isVip) NoorGold else NoorTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun DramaPosterCard(
    drama: Drama,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Int = 135,
    height: Int = 200
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NoorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
            .testTag("drama_card_${drama.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val posterRes = when {
                drama.id.contains("billionaire") -> R.drawable.poster_billionaire_bride
                drama.id.contains("revenge") -> R.drawable.poster_revenge_ex_wife
                drama.id.contains("heiress") -> R.drawable.poster_hidden_heiress
                else -> R.drawable.poster_billionaire_bride
            }
            Image(
                painter = painterResource(id = posterRes),
                contentDescription = drama.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.95f)
                            ),
                            startY = 60f
                        )
                    )
            )

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (drama.isTrending) {
                    Surface(
                        color = NoorCoral,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "TRENDING",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                } else if (drama.isExclusive) {
                    Surface(
                        color = NoorPurple,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "EXCLUSIVE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (drama.isVipOnly) {
                    Surface(
                        color = NoorGold,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "VIP",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = NoorPrimaryBackground,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Bottom Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = drama.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = NoorGold,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "${drama.rating}",
                        color = NoorGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        color = NoorTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${drama.totalEpisodes} Ep",
                        color = NoorTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBanner(
    drama: Drama,
    onPlayClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onListToggle: () -> Unit,
    isBookmarked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .testTag("hero_banner")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val posterRes = when {
                drama.id.contains("billionaire") -> R.drawable.poster_billionaire_bride
                drama.id.contains("revenge") -> R.drawable.poster_revenge_ex_wife
                drama.id.contains("heiress") -> R.drawable.poster_hidden_heiress
                else -> R.drawable.poster_billionaire_bride
            }
            Image(
                painter = painterResource(id = posterRes),
                contentDescription = drama.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Deep cinematic gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x60080B14),
                                Color(0xF0080B14),
                                NoorPrimaryBackground
                            ),
                            startY = 100f
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
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
                            text = "#1 TRENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = NoorPrimaryBackground,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = NoorCardBackground.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, NoorBorder)
                    ) {
                        Text(
                            text = drama.genre,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = NoorTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = drama.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 28.sp
                )

                if (drama.originalTitle.isNotBlank()) {
                    Text(
                        text = drama.originalTitle,
                        fontSize = 13.sp,
                        color = NoorGoldLight,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = drama.synopsis,
                    fontSize = 12.sp,
                    color = NoorTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NoorGold,
                            contentColor = NoorPrimaryBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("hero_play_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play Ep 1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onListToggle,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = NoorCardBackground.copy(alpha = 0.7f),
                            contentColor = if (isBookmarked) NoorGold else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isBookmarked) NoorGold else NoorBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp).testTag("hero_list_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "My List",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDetailsClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NoorCardBackground.copy(alpha = 0.7f))
                            .border(1.dp, NoorBorder, RoundedCornerShape(12.dp))
                            .testTag("hero_details_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Details",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NoorGold)
            )
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (onSeeAllClick != null) {
            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "See All",
                    color = NoorGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "See All",
                    tint = NoorGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EpisodeItemRow(
    episode: Episode,
    isCurrent: Boolean,
    isVipUser: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAccessible = episode.isUnlocked || isVipUser || episode.accessType == AccessType.FREE

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) NoorElevatedSurface else NoorCardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) NoorGold else NoorBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("episode_item_${episode.episodeNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail / Ep Number badge
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NoorElevatedSurface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_noorflix_logo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                if (isCurrent) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Playing",
                        tint = NoorGold,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (!isAccessible) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = NoorGold,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "EP ${episode.episodeNumber}",
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) NoorGold else Color.White,
                        fontSize = 14.sp
                    )
                    when (episode.accessType) {
                        AccessType.FREE -> {
                            Surface(
                                color = NoorSuccess.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FREE",
                                    color = NoorSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        AccessType.VIP_ONLY -> {
                            Surface(
                                color = NoorGoldDark.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "VIP ONLY",
                                    color = NoorGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        AccessType.COIN_UNLOCK -> {
                            if (!episode.isUnlocked) {
                                Surface(
                                    color = NoorElevatedSurface,
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, NoorGoldDark)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = NoorGold,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = " ${episode.coinPrice}",
                                            color = NoorGoldLight,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = episode.title,
                    fontSize = 12.sp,
                    color = NoorTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Duration
            Text(
                text = "${episode.durationSeconds / 60}m ${episode.durationSeconds % 60}s",
                fontSize = 11.sp,
                color = NoorTextMuted
            )
        }
    }
}
