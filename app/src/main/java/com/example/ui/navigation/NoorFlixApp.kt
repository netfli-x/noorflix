package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.details.DramaDetailsScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.mylist.MyListScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.player.VerticalDramaPlayerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.vip.VipMembershipScreen
import com.example.ui.screens.wallet.WalletScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Discover : Screen("discover")
    object MyList : Screen("my_list")
    object Wallet : Screen("wallet")
    object Profile : Screen("profile")
    object Details : Screen("details")
    object Player : Screen("player")
    object Vip : Screen("vip")
    object Search : Screen("search")
    object Notifications : Screen("notifications")
    object Admin : Screen("admin")
}

data class BottomNavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun NoorFlixApp(viewModel: NoorFlixViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    val isArabic by viewModel.isArabic.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle Toast / Snackbar messages
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = msg.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearMessage()
            }
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.Discover, "Discover", Icons.Filled.Explore, Icons.Outlined.Explore),
        BottomNavItem(Screen.MyList, "My List", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
        BottomNavItem(Screen.Wallet, "Wallet", Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn),
        BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val isBottomBarVisible = when (currentScreen) {
        Screen.Home, Screen.Discover, Screen.MyList, Screen.Wallet, Screen.Profile -> true
        else -> false
    }

    // Handle System Back Button
    BackHandler(enabled = currentScreen !in listOf(Screen.Home, Screen.Splash, Screen.Auth)) {
        currentScreen = Screen.Home
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = NoorElevatedSurface,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            bottomBar = {
                if (isBottomBarVisible) {
                    NavigationBar(
                        containerColor = NoorSecondaryBackground,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("main_bottom_nav")
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentScreen = item.screen },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) NoorGold else NoorTextMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (isSelected) NoorGold else NoorTextMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = NoorGold.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            },
            containerColor = NoorPrimaryBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isBottomBarVisible) paddingValues else PaddingValues(0.dp))
            ) {
                when (currentScreen) {
                    Screen.Splash -> {
                        SplashScreen(
                            onSplashComplete = { currentScreen = Screen.Onboarding }
                        )
                    }

                    Screen.Onboarding -> {
                        OnboardingScreen(
                            onFinishOnboarding = { currentScreen = Screen.Home }
                        )
                    }

                    Screen.Auth -> {
                        AuthScreen(
                            viewModel = viewModel,
                            onAuthSuccess = { currentScreen = Screen.Home }
                        )
                    }

                    Screen.Home -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onDramaClick = { drama ->
                                viewModel.selectDrama(drama)
                                currentScreen = Screen.Details
                            },
                            onPlayDrama = { drama ->
                                viewModel.selectDrama(drama)
                                val firstEp = viewModel.currentDramaEpisodes.value.firstOrNull()
                                if (firstEp != null) {
                                    viewModel.selectEpisode(firstEp)
                                }
                                currentScreen = Screen.Player
                            },
                            onNavigateToSearch = { currentScreen = Screen.Search },
                            onNavigateToNotifications = { currentScreen = Screen.Notifications },
                            onNavigateToWallet = { currentScreen = Screen.Wallet },
                            onNavigateToVip = { currentScreen = Screen.Vip },
                            onSeeAllCategory = {
                                currentScreen = Screen.Discover
                            }
                        )
                    }

                    Screen.Discover -> {
                        DiscoverScreen(
                            viewModel = viewModel,
                            onDramaClick = { drama ->
                                viewModel.selectDrama(drama)
                                currentScreen = Screen.Details
                            }
                        )
                    }

                    Screen.MyList -> {
                        MyListScreen(
                            viewModel = viewModel,
                            onDramaClick = { drama ->
                                viewModel.selectDrama(drama)
                                currentScreen = Screen.Details
                            },
                            onExploreClick = { currentScreen = Screen.Discover }
                        )
                    }

                    Screen.Wallet -> {
                        WalletScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = Screen.Home },
                            onNavigateToVip = { currentScreen = Screen.Vip }
                        )
                    }

                    Screen.Profile -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToWallet = { currentScreen = Screen.Wallet },
                            onNavigateToVip = { currentScreen = Screen.Vip },
                            onNavigateToMyList = { currentScreen = Screen.MyList },
                            onNavigateToAdmin = { currentScreen = Screen.Admin },
                            onNavigateToAuth = { currentScreen = Screen.Auth }
                        )
                    }

                    Screen.Details -> {
                        val drama = viewModel.selectedDrama.collectAsState().value
                        if (drama != null) {
                            DramaDetailsScreen(
                                drama = drama,
                                viewModel = viewModel,
                                onBackClick = { currentScreen = Screen.Home },
                                onPlayEpisode = { ep ->
                                    viewModel.selectEpisode(ep)
                                    currentScreen = Screen.Player
                                },
                                onNavigateToWallet = { currentScreen = Screen.Wallet },
                                onNavigateToVip = { currentScreen = Screen.Vip }
                            )
                        } else {
                            currentScreen = Screen.Home
                        }
                    }

                    Screen.Player -> {
                        VerticalDramaPlayerScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = Screen.Details },
                            onNavigateToWallet = { currentScreen = Screen.Wallet },
                            onNavigateToVip = { currentScreen = Screen.Vip }
                        )
                    }

                    Screen.Vip -> {
                        VipMembershipScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = Screen.Home }
                        )
                    }

                    Screen.Search -> {
                        SearchScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = Screen.Home },
                            onDramaClick = { drama ->
                                viewModel.selectDrama(drama)
                                currentScreen = Screen.Details
                            },
                            onPlayDrama = { drama ->
                                viewModel.selectDrama(drama)
                                val firstEp = viewModel.currentDramaEpisodes.value.firstOrNull()
                                if (firstEp != null) {
                                    viewModel.selectEpisode(firstEp)
                                }
                                currentScreen = Screen.Player
                            }
                        )
                    }

                    Screen.Notifications -> {
                        NotificationsScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = Screen.Home }
                        )
                    }

                    Screen.Admin -> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = Screen.Profile }
                        )
                    }
                }
            }
        }
    }
}
