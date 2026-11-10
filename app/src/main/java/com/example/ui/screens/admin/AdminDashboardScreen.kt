package com.example.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccessType
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.ui.screens.home.HomeScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.HomeCmsConfig
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: NoorFlixViewModel,
    onBackClick: () -> Unit
) {
    val allDramas by viewModel.allDramas.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cmsConfig by viewModel.cmsConfig.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    // 0: Overview & Analytics, 1: Drama Management, 2: Users & Wallet, 3: Home CMS

    var showAddDramaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(NoorPrimaryBackground)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("NoorFlix Control Center", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Administrator Dashboard • Super Admin", color = NoorGold, fontSize = 11.sp)
                }
            }
        },
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Admin Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = NoorCardBackground,
                contentColor = NoorGold,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NoorGold
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Dramas (${allDramas.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Users & Coins") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Home CMS") })
            }

            when (selectedTab) {
                0 -> AdminOverviewTab(allDramas)
                1 -> AdminDramasTab(allDramas, onAddClick = { showAddDramaDialog = true }, onDeleteClick = { viewModel.adminDeleteDrama(it) })
                2 -> AdminUsersTab(currentUser, onGrantCoins = { viewModel.adminGrantCoins(it) }, onToggleVip = { viewModel.adminToggleVip() })
                3 -> AdminCmsTab(cmsConfig, allDramas, onUpdateConfig = { viewModel.updateCmsConfig(it) })
            }
        }

        if (showAddDramaDialog) {
            AddDramaDialog(
                onDismiss = { showAddDramaDialog = false },
                onConfirm = { newDrama ->
                    viewModel.adminAddDrama(newDrama)
                    showAddDramaDialog = false
                }
            )
        }
    }
}

@Composable
private fun AdminOverviewTab(dramas: List<Drama>) {
    val totalEpisodes = dramas.sumOf { it.totalEpisodes }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Real-Time Platform Performance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("Total Dramas", "${dramas.size}", NoorGold, Modifier.weight(1f))
                AdminStatCard("Total Episodes", "$totalEpisodes", NoorPurpleLight, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("Total Video Views", "1.42M", NoorCoral, Modifier.weight(1f))
                AdminStatCard("Active Users", "18,450", NoorSuccess, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("VIP Subscribers", "3,820", NoorGoldLight, Modifier.weight(1f))
                AdminStatCard("Monthly Revenue", "$48,920", NoorGold, Modifier.weight(1f))
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Streaming Infrastructure Health", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Video CDN Edge: Operational (Google Cloud Storage / HLS)", color = NoorSuccess, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• ExoPlayer Adaptive Bitrate: Normal", color = NoorTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• DRM & Coin Validation Server: 99.98% Uptime", color = NoorTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Database Latency: 12ms", color = NoorTextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = NoorTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = accentColor, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun AdminDramasTab(
    dramas: List<Drama>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Manage Catalog", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Drama", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        items(dramas) { drama ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(drama.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${drama.genre} • ${drama.totalEpisodes} Episodes • Rating: ${drama.rating}", color = NoorTextMuted, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                            if (drama.isFeatured) Surface(color = NoorGold.copy(0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("FEATURED", color = NoorGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(3.dp))
                            }
                            if (drama.isTrending) Surface(color = NoorCoral.copy(0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("TRENDING", color = NoorCoral, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(3.dp))
                            }
                        }
                    }

                    IconButton(onClick = { onDeleteClick(drama.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NoorCoral)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersTab(
    currentUser: com.example.data.model.UserProfile,
    onGrantCoins: (Int) -> Unit,
    onToggleVip: () -> Unit
) {
    var grantAmount by remember { mutableStateOf("100") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("User Administration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Profile: ${currentUser.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Email: ${currentUser.email}", color = NoorTextSecondary, fontSize = 13.sp)
                    Text("Coins: ${currentUser.coins} | VIP: ${currentUser.isVip}", color = NoorGoldLight, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = grantAmount,
                            onValueChange = { grantAmount = it },
                            label = { Text("Coins") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Button(
                            onClick = { grantAmount.toIntOrNull()?.let { onGrantCoins(it) } },
                            colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Grant Coins")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onToggleVip,
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentUser.isVip) NoorCoral else NoorPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (currentUser.isVip) "Revoke VIP Status" else "Grant 1-Year VIP Status")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCmsTab(
    config: HomeCmsConfig,
    dramas: List<Drama>,
    onUpdateConfig: (HomeCmsConfig) -> Unit
) {
    var selectedHeroId by remember { mutableStateOf(config.heroDramaId) }
    var showTrending by remember { mutableStateOf(config.showTrendingRow) }
    var showOriginals by remember { mutableStateOf(config.showOriginalsRow) }
    var showBillionaire by remember { mutableStateOf(config.showBillionaireRow) }
    var showRevenge by remember { mutableStateOf(config.showRevengeRow) }
    var showVip by remember { mutableStateOf(config.showVipRow) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Home Screen Content Management System", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Hero Banner Drama:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    dramas.forEach { drama ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedHeroId == drama.id,
                                onClick = { selectedHeroId = drama.id },
                                colors = RadioButtonDefaults.colors(selectedColor = NoorGold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(drama.title, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Toggle Home Rows:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    RowToggle("Trending Row", showTrending) { showTrending = it }
                    RowToggle("Originals Row", showOriginals) { showOriginals = it }
                    RowToggle("Billionaire Row", showBillionaire) { showBillionaire = it }
                    RowToggle("Revenge Row", showRevenge) { showRevenge = it }
                    RowToggle("VIP Exclusive Row", showVip) { showVip = it }
                }
            }
        }

        item {
            Button(
                onClick = {
                    onUpdateConfig(
                        HomeCmsConfig(
                            heroDramaId = selectedHeroId,
                            showTrendingRow = showTrending,
                            showOriginalsRow = showOriginals,
                            showBillionaireRow = showBillionaire,
                            showRevengeRow = showRevenge,
                            showVipRow = showVip
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save & Apply CMS Config", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RowToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = NoorTextSecondary, fontSize = 13.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NoorGold, checkedTrackColor = NoorElevatedSurface)
        )
    }
}

@Composable
private fun AddDramaDialog(
    onDismiss: () -> Unit,
    onConfirm: (Drama) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var originalTitle by remember { mutableStateOf("") }
    var synopsis by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Billionaire Romance") }
    var episodesCount by remember { mutableStateOf("12") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish New Mini Drama", color = Color.White, fontWeight = FontWeight.Bold) },
        containerColor = NoorElevatedSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("English Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = originalTitle,
                    onValueChange = { originalTitle = it },
                    label = { Text("Original / Arabic Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("Synopsis") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = episodesCount,
                    onValueChange = { episodesCount = it },
                    label = { Text("Total Episodes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val eps = episodesCount.toIntOrNull() ?: 10
                        onConfirm(
                            Drama(
                                id = "drama_${System.currentTimeMillis()}",
                                title = title,
                                originalTitle = originalTitle,
                                synopsis = synopsis.ifBlank { "Exciting new mini drama series streaming on NoorFlix." },
                                posterUrl = "",
                                genre = genre,
                                totalEpisodes = eps,
                                rating = 4.9,
                                viewsCount = 1000,
                                releaseYear = 2024,
                                isFeatured = false,
                                isTrending = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground)
            ) {
                Text("Publish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NoorTextMuted)
            }
        }
    )
}
