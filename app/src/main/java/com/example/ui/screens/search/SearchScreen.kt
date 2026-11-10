package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Drama
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun SearchScreen(
    viewModel: NoorFlixViewModel,
    onBackClick: () -> Unit,
    onDramaClick: (Drama) -> Unit,
    onPlayDrama: (Drama) -> Unit
) {
    val allDramas by viewModel.allDramas.collectAsState()
    var query by remember { mutableStateOf("") }

    val trendingKeywords = listOf("Billionaire", "Ex-Wife", "Secret Bride", "CEO", "Sheikh", "Contract Marriage", "Revenge")

    val searchResults = remember(allDramas, query) {
        if (query.isBlank()) emptyList()
        else {
            allDramas.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.originalTitle.contains(query, ignoreCase = true) ||
                        it.genre.contains(query, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                        it.cast.any { cast -> cast.contains(query, ignoreCase = true) }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(NoorPrimaryBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search dramas, actors, genres...", color = NoorTextMuted, fontSize = 14.sp) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = NoorTextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NoorElevatedSurface,
                        unfocusedContainerColor = NoorCardBackground,
                        focusedBorderColor = NoorGold,
                        unfocusedBorderColor = NoorBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_field")
                )
            }
        },
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Trending keywords
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = NoorGold, modifier = Modifier.size(16.dp))
                    Text("Trending Searches", color = NoorGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trendingKeywords) { keyword ->
                        Surface(
                            onClick = { query = keyword },
                            shape = RoundedCornerShape(16.dp),
                            color = NoorCardBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder)
                        ) {
                            Text(
                                text = keyword,
                                color = NoorTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Results
            if (query.isNotBlank()) {
                item {
                    Text(
                        text = "Results for \"$query\" (${searchResults.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No matches found for \"$query\".",
                                color = NoorTextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(searchResults) { drama ->
                        Card(
                            onClick = { onDramaClick(drama) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                            modifier = Modifier.fillMaxWidth().testTag("search_result_${drama.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = drama.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${drama.genre} • ${drama.totalEpisodes} Episodes • ★ ${drama.rating}",
                                        color = NoorGoldLight,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = drama.synopsis,
                                        color = NoorTextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }

                                IconButton(
                                    onClick = { onPlayDrama(drama) },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = NoorGold)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
