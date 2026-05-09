package com.aibrowser.app.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aibrowser.app.domain.model.*
import com.aibrowser.app.presentation.ui.components.*
import com.aibrowser.app.presentation.ui.theme.AITheme
import com.aibrowser.app.presentation.viewmodel.AuthViewModel
import com.aibrowser.app.presentation.viewmodel.BrowserViewModel
import com.aibrowser.app.presentation.viewmodel.VPNViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToBrowser: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToVPN: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    browserViewModel: BrowserViewModel = hiltViewModel(),
    vpnViewModel: VPNViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val isVPNConnected by vpnViewModel.isConnected.collectAsState()
    val tabs by browserViewModel.tabs.collectAsState()
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = AITheme.DarkSurface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Already on home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAI,
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "AI") },
                    label = { Text("AI") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToVPN,
                    icon = {
                        Icon(
                            if (isVPNConnected) Icons.Default.VpnKey else Icons.Default.VpnKey,
                            contentDescription = "VPN",
                            tint = if (isVPNConnected) AITheme.AccentGreen else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("VPN") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AITheme.DarkBackground)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AITheme.TextSecondary
                    )
                    Text(
                        text = currentUser?.username ?: "User",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AITheme.TextPrimary
                    )
                }
                
                Row {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = AITheme.TextPrimary
                        )
                    }
                }
            }
            
            // Quick Actions
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickActionCard(
                        icon = Icons.Default.Language,
                        title = "Browse",
                        subtitle = "${tabs.size} tabs open",
                        onClick = onNavigateToBrowser,
                        modifier = Modifier.width(160.dp),
                        gradientColors = listOf(AITheme.AccentPurple, AITheme.AccentBlue)
                    )
                }
                item {
                    QuickActionCard(
                        icon = Icons.Default.Psychology,
                        title = "AI Chat",
                        subtitle = "Ask anything",
                        onClick = onNavigateToAI,
                        modifier = Modifier.width(160.dp),
                        gradientColors = listOf(AITheme.AccentCyan, AITheme.AccentGreen)
                    )
                }
                item {
                    QuickActionCard(
                        icon = if (isVPNConnected) Icons.Default.Shield else Icons.Default.VpnKey,
                        title = if (isVPNConnected) "VPN On" else "VPN",
                        subtitle = "Tap to connect",
                        onClick = onNavigateToVPN,
                        modifier = Modifier.width(160.dp),
                        gradientColors = listOf(AITheme.AccentOrange, AITheme.AccentRed)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recent Tabs
            SectionHeader(title = "Recent Tabs", action = "See All", onActionClick = onNavigateToBrowser)
            
            if (tabs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = AITheme.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tabs yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AITheme.TextSecondary
                        )
                        TextButton(onClick = onNavigateToBrowser) {
                            Text("Open Browser")
                        }
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tabs.take(5)) { tab ->
                        TabCard(
                            tab = tab,
                            onClick = onNavigateToBrowser
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Links
            SectionHeader(title = "Quick Links")
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickLinkItem(
                    icon = Icons.Default.Bookmark,
                    title = "Bookmarks",
                    onClick = onNavigateToBookmarks
                )
                QuickLinkItem(
                    icon = Icons.Default.Download,
                    title = "Downloads",
                    onClick = onNavigateToDownloads
                )
                QuickLinkItem(
                    icon = Icons.Default.History,
                    title = "History",
                    onClick = onNavigateToHistory
                )
                QuickLinkItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun TabCard(
    tab: Tab,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tab.isIncognito) AITheme.DarkSurfaceVariant else AITheme.DarkCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = AITheme.TextSecondary
                )
                if (tab.isIncognito) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AITheme.AccentPurple.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Incognito",
                            style = MaterialTheme.typography.labelSmall,
                            color = AITheme.AccentPurple,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tab.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = AITheme.TextPrimary,
                maxLines = 1
            )
            Text(
                text = tab.url,
                style = MaterialTheme.typography.bodySmall,
                color = AITheme.TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickLinkItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = AITheme.DarkSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = AITheme.AccentPurple
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AITheme.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AITheme.TextSecondary
            )
        }
    }
}
