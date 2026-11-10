package com.example.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.DramaPosterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun DiscoverScreen(
    viewModel: NoorFlixViewModel,
    onDramaClick: (Drama) -> Unit
) {
    val allDramas by viewModel.allDramas.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedAccess by remember { mutableStateOf("All") } // "All", "Free", "VIP Only"
    var selectedSort by remember { mutableStateOf("Most Popular") }

    val genres = listOf("All", "Billionaire", "Romance", "Revenge", "CEO", "Mystery", "Arabic")
    val accessFilters = listOf("All", "Free Episodes", "VIP Only", "Completed")

    val filteredDramas = remember(allDramas, searchQuery, selectedGenre, selectedAccess, selectedSort) {
        var list = allDramas

        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.originalTitle.contains(searchQuery, ignoreCase = true) ||
                        it.synopsis.contains(searchQuery, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
            }
        }

        if (selectedGenre != "All") {
            list = list.filter { it.genre.contains(selectedGenre, ignoreCase = true) || it.tags.any { t -> t.contains(selectedGenre, ignoreCase = true) } }
        }

        when (selectedAccess) {
            "VIP Only" -> list = list.filter { it.isVipOnly }
            "Free Episodes" -> list = list.filter { !it.isVipOnly }
            "Completed" -> list = list.filter { it.status.name == "COMPLETED" }
        }

        when (selectedSort) {
            "Most Popular" -> list.sortedByDescending { it.viewsCount }
            "Top Rated" -> list.sortedByDescending { it.rating }
            "Newest" -> list.sortedByDescending { it.releaseYear }
            else -> list
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(NoorPrimaryBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Discover Dramas",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search title, actor, or genre...", color = NoorTextMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NoorGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
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
                        .fillMaxWidth()
                        .testTag("discover_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Genre Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = genre },
                            label = { Text(genre, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NoorGold,
                                selectedLabelColor = NoorPrimaryBackground,
                                containerColor = NoorCardBackground,
                                labelColor = NoorTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedGenre == genre,
                                borderColor = if (selectedGenre == genre) NoorGold else NoorBorder
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Access Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(accessFilters) { filter ->
                        FilterChip(
                            selected = selectedAccess == filter,
                            onClick = { selectedAccess = filter },
                            label = { Text(filter, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NoorPurple,
                                selectedLabelColor = Color.White,
                                containerColor = NoorCardBackground.copy(alpha = 0.6f),
                                labelColor = NoorTextMuted
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        },
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Count & Sort row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredDramas.size} Dramas Found",
                    color = NoorTextMuted,
                    fontSize = 12.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = NoorGold, modifier = Modifier.size(14.dp))
                    Text(
                        text = selectedSort,
                        color = NoorGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredDramas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Dramas Found",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try clearing filters or search with another keyword.",
                            color = NoorTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize().testTag("discover_grid")
                ) {
                    items(filteredDramas) { drama ->
                        DramaPosterCard(
                            drama = drama,
                            onClick = { onDramaClick(drama) },
                            width = 110,
                            height = 175
                        )
                    }
                }
            }
        }
    }
}
