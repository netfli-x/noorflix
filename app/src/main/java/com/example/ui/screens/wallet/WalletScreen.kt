package com.example.ui.screens.wallet

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinPackage
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: NoorFlixViewModel,
    onBackClick: () -> Unit,
    onNavigateToVip: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    val coinPackages = listOf(
        CoinPackage("pkg_1", 100, 0, 0.99, null),
        CoinPackage("pkg_2", 500, 100, 4.99, "POPULAR"),
        CoinPackage("pkg_3", 1200, 300, 9.99, "BEST VALUE"),
        CoinPackage("pkg_4", 3000, 1000, 19.99, "MEGA PACK")
    )

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
                Text(
                    text = "Coins & Wallet",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
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
                .testTag("wallet_scroll_container"),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorGoldDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wallet_balance_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(NoorGoldDark.copy(alpha = 0.25f), Color.Transparent),
                                    radius = 350f
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Current Balance", color = NoorTextSecondary, fontSize = 13.sp)
                                if (user.isVip) {
                                    Surface(
                                        color = NoorGold,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "VIP ACTIVE",
                                            color = NoorPrimaryBackground,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NoorGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = NoorGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "${user.coins}",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Coins",
                                    color = NoorGoldLight,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.claimDailyReward() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Daily Check-In (+40)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.watchRewardedAd() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = NoorPurple, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Watch & Earn (+20)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Coin Packages Grid
            item {
                Text(
                    text = "Recharge Coins",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    coinPackages.forEach { pkg ->
                        Card(
                            onClick = { viewModel.buyCoins(pkg) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NoorCardBackground),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (pkg.tag != null) NoorGold else NoorBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = NoorGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${pkg.coins} Coins",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            if (pkg.bonusCoins > 0) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = NoorCoral.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "+${pkg.bonusCoins} FREE",
                                                        color = NoorCoral,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        pkg.tag?.let {
                                            Text(it, color = NoorGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.buyCoins(pkg) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (pkg.tag != null) NoorGold else NoorElevatedSurface,
                                        contentColor = if (pkg.tag != null) NoorPrimaryBackground else Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Text("$${pkg.priceUsd}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // VIP Upsell Banner
            item {
                Card(
                    onClick = onNavigateToVip,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorPurple),
                    modifier = Modifier.fillMaxWidth()
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
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = NoorGold, modifier = Modifier.size(32.dp))
                            Column {
                                Text("Want Unlimited Episodes?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Upgrade to NoorFlix VIP membership", color = NoorTextSecondary, fontSize = 12.sp)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NoorGold)
                    }
                }
            }

            // Transaction History
            item {
                Text(
                    text = "Transaction History",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (transactions.isEmpty()) {
                item {
                    Text("No transactions yet.", color = NoorTextMuted, fontSize = 13.sp)
                }
            } else {
                items(transactions) { tx ->
                    val dateStr = remember(tx.timestamp) {
                        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                    }
                    val isCredit = tx.amount > 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NoorCardBackground, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tx.description, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(dateStr, color = NoorTextMuted, fontSize = 11.sp)
                        }
                        Text(
                            text = if (isCredit) "+${tx.amount}" else "${tx.amount}",
                            color = if (isCredit) NoorSuccess else NoorCoral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
