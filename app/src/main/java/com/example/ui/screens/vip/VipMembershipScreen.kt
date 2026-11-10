package com.example.ui.screens.vip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VipPlan
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun VipMembershipScreen(
    viewModel: NoorFlixViewModel,
    onBackClick: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()

    val plans = listOf(
        VipPlan("plan_weekly", "Weekly Pass", 7, 4.99, null, listOf("Access to all episodes for 7 days", "HD Streaming")),
        VipPlan("plan_monthly", "Monthly VIP", 30, 14.99, "MOST POPULAR", listOf("Unlimited episode access for 30 days", "Ad-free experience", "Exclusive Originals", "VIP Badge")),
        VipPlan("plan_yearly", "Annual Elite", 365, 79.99, "SAVE 55%", listOf("1 Full Year VIP status", "VIP badge", "Offline downloads", "Early releases access"))
    )

    var selectedPlanId by remember { mutableStateOf("plan_monthly") }
    val selectedPlan = plans.first { it.id == selectedPlanId }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(NoorPrimaryBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("NoorFlix VIP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
        },
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("vip_screen_container"),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Graphic
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorElevatedSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NoorGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(NoorGoldDark.copy(alpha = 0.35f), NoorPrimaryBackground)
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(NoorGold.copy(alpha = 0.2f))
                                    .border(1.dp, NoorGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "VIP",
                                    tint = NoorGold,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "UNLOCK ALL DRAMAS",
                                color = NoorGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Watch every episode without limits, coins, or interruptions.",
                                color = NoorTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Benefits Checklist
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NoorCardBackground, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        "Unlimited access to ALL locked episodes",
                        "100% Ad-Free cinematic streaming",
                        "New mini-drama episodes 48 hours early",
                        "Full 1080p Ultra HD vertical playback",
                        "Golden VIP Profile flair"
                    ).forEach { benefit ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(NoorGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NoorPrimaryBackground, modifier = Modifier.size(14.dp))
                            }
                            Text(benefit, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Plans Selector
            item {
                Text("Select Membership Plan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    plans.forEach { plan ->
                        val isSelected = plan.id == selectedPlanId
                        Card(
                            onClick = { selectedPlanId = plan.id },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NoorElevatedSurface else NoorCardBackground
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) NoorGold else NoorBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(plan.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        plan.savingsText?.let { tag ->
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = NoorCoral,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(tag, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${plan.durationDays} Days of VIP Privileges", color = NoorTextMuted, fontSize = 12.sp)
                                }

                                Text(
                                    text = "$${plan.priceUsd}",
                                    color = if (isSelected) NoorGold else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Subscribe Button CTA
            item {
                Button(
                    onClick = { viewModel.subscribeVip(selectedPlan) },
                    colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("subscribe_vip_button")
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Join VIP for $${selectedPlan.priceUsd}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Recurring billing. Cancel anytime in Google Play Store settings.",
                    color = NoorTextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
