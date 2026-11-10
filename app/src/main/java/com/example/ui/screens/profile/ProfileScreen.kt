package com.example.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun ProfileScreen(
    viewModel: NoorFlixViewModel,
    onNavigateToWallet: () -> Unit,
    onNavigateToVip: () -> Unit,
    onNavigateToMyList: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(user.notificationsEnabled) }

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
                Text(
                    text = "My Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }
        },
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("profile_scroll_container"),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                    modifier = Modifier.fillMaxWidth().testTag("profile_user_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(NoorElevatedSurface)
                                .border(2.dp, if (user.isVip) NoorGold else NoorBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_noorflix_logo),
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.username,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                if (user.isVip) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "VIP",
                                        tint = NoorGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user.email,
                                color = NoorTextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = NoorPurple.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Role: ${user.role.name}",
                                    color = NoorPurpleLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Stats (Coins & VIP)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Coins Card
                    Card(
                        onClick = onNavigateToWallet,
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NoorGoldDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = NoorGold)
                                Text("Recharge", color = NoorGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${user.coins}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                            Text("Coins Balance", color = NoorTextMuted, fontSize = 11.sp)
                        }
                    }

                    // VIP Status Card
                    Card(
                        onClick = onNavigateToVip,
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (user.isVip) NoorGold else NoorBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = if (user.isVip) NoorGold else NoorTextMuted)
                                Text(if (user.isVip) "Active" else "Upgrade", color = if (user.isVip) NoorGold else NoorTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(if (user.isVip) "VIP Member" else "Free Plan", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text(if (user.isVip) "Unlimited Access" else "Limited Access", color = NoorTextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Language & RTL Settings Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = NoorGold)
                                Column {
                                    Text("Display Language / اللغة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(if (isArabic) "العربية (RTL)" else "English (LTR)", color = NoorTextSecondary, fontSize = 12.sp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = !isArabic,
                                    onClick = { viewModel.setLanguage("English") },
                                    label = { Text("EN", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NoorGold,
                                        selectedLabelColor = NoorPrimaryBackground
                                    )
                                )
                                FilterChip(
                                    selected = isArabic,
                                    onClick = { viewModel.setLanguage("العربية") },
                                    label = { Text("عربي", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NoorGold,
                                        selectedLabelColor = NoorPrimaryBackground
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Admin Control Center Portal
            item {
                Card(
                    onClick = onNavigateToAdmin,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorGoldDark),
                    modifier = Modifier.fillMaxWidth().testTag("profile_admin_portal_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NoorGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = NoorGold)
                            }
                            Column {
                                Text("Admin Control Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Manage dramas, episodes, users & CMS", color = NoorTextSecondary, fontSize = 12.sp)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NoorGold)
                    }
                }
            }

            // General Menu Options
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NoorCardBackground, RoundedCornerShape(16.dp))
                        .padding(vertical = 4.dp)
                ) {
                    ProfileMenuItem(
                        icon = Icons.Default.Bookmark,
                        title = "My List & Bookmarks",
                        onClick = onNavigateToMyList
                    )
                    Divider(color = NoorBorder, thickness = 0.5.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                    viewModel.showMessage("Notification preference updated.")
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NoorGold,
                                    checkedTrackColor = NoorElevatedSurface
                                )
                            )
                        }
                    )
                    Divider(color = NoorBorder, thickness = 0.5.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Help & Support",
                        onClick = { showHelpDialog = true }
                    )
                    Divider(color = NoorBorder, thickness = 0.5.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.Logout,
                        title = "Log Out",
                        iconColor = NoorCoral,
                        titleColor = NoorCoral,
                        onClick = onNavigateToAuth
                    )
                }
            }
        }

        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("NoorFlix Support", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "For account assistance, video playback questions, or VIP subscription inquiries, please contact:\n\nsupport@noorflix.com\n\nVersion 1.0.0 (Production Build)",
                        color = NoorTextSecondary,
                        fontSize = 13.sp
                    )
                },
                containerColor = NoorElevatedSurface,
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("OK", color = NoorGold)
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    iconColor: Color = NoorTextSecondary,
    titleColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            Text(title, color = titleColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NoorTextMuted, modifier = Modifier.size(20.dp))
        }
    }
}
