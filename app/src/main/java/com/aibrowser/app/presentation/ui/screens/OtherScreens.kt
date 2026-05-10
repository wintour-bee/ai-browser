package com.aibrowser.app.presentation.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aibrowser.app.domain.model.*
import com.aibrowser.app.domain.usecase.*
import com.aibrowser.app.presentation.ui.components.*
import com.aibrowser.app.presentation.ui.theme.AITheme
import com.aibrowser.app.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AITheme.TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AITheme.TextPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AITheme.DarkBackground),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Appearance Section
            item {
                SectionHeader(title = "Appearance")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    onClick = { },
                    trailing = {
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AITheme.AccentPurple,
                                checkedTrackColor = AITheme.AccentPurple.copy(alpha = 0.5f)
                            )
                        )
                    }
                )
            }
            
            // Browser Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Browser")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Search,
                    title = "Default Search Engine",
                    subtitle = "Google",
                    onClick = { }
                )
            }
            
            item {
                SwitchSettingsItem(
                    icon = Icons.Default.Block,
                    title = "Ad Blocker",
                    subtitle = "Block ads and popups",
                    checked = true,
                    onCheckedChange = { }
                )
            }
            
            item {
                SwitchSettingsItem(
                    icon = Icons.Default.VisibilityOff,
                    title = "Incognito Mode",
                    subtitle = "Default to private browsing",
                    checked = false,
                    onCheckedChange = { }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Fingerprint,
                    title = "User Agent",
                    subtitle = "Chrome on Windows",
                    onClick = { }
                )
            }
            
            // VPN Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "VPN")
            }
            
            item {
                SwitchSettingsItem(
                    icon = Icons.Default.VpnKey,
                    title = "Auto Connect",
                    subtitle = "Connect when app starts",
                    checked = false,
                    onCheckedChange = { }
                )
            }
            
            item {
                SwitchSettingsItem(
                    icon = Icons.Default.Shield,
                    title = "Kill Switch",
                    subtitle = "Block internet if VPN disconnects",
                    checked = true,
                    onCheckedChange = { }
                )
            }
            
            item {
                SwitchSettingsItem(
                    icon = Icons.Default.Dns,
                    title = "DNS Leak Protection",
                    subtitle = "Prevent DNS leaks",
                    checked = true,
                    onCheckedChange = { }
                )
            }
            
            // AI Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "AI")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Psychology,
                    title = "Default AI Provider",
                    subtitle = "ChatGPT",
                    onClick = { }
                )
            }
            
            // Account Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Account")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Sync,
                    title = "Sync Data",
                    subtitle = "Last synced: Never",
                    onClick = { }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Vibration,
                    title = "Notifications",
                    onClick = { }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy & Security",
                    onClick = { }
                )
            }
            
            // About Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "About")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = { }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    onClick = { }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = { }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onHistoryItemClick: (String) -> Unit
) {
    // Use a simple placeholder for history - this would typically be connected to a ViewModel
    val history by remember { mutableStateOf(emptyList<HistoryItem>()) }.collectAsState()
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = AITheme.TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AITheme.TextPrimary
                        )
                    }
                    
                    TextButton(onClick = { /* Clear all */ }) {
                        Text(
                            text = "Clear All",
                            color = AITheme.AccentRed
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AITheme.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AITheme.TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(history) { item ->
                    HistoryItem(
                        item = item,
                        onClick = { onHistoryItemClick(item.url) },
                        onDelete = { /* Delete */ }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    item: HistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AITheme.DarkSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = AITheme.TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AITheme.TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = item.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = AITheme.TextSecondary,
                    maxLines = 1
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp),
                    tint = AITheme.TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onNavigateBack: () -> Unit,
    onBookmarkClick: (String) -> Unit
) {
    // Use a simple placeholder for bookmarks - this would typically be connected to a ViewModel
    val bookmarks by remember { mutableStateOf(emptyList<Bookmark>()) }.collectAsState()
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AITheme.TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bookmarks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AITheme.TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(onClick = { /* Add bookmark */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = AITheme.AccentPurple
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AITheme.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No bookmarks yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AITheme.TextSecondary
                    )
                    Text(
                        text = "Save pages to access them quickly",
                        style = MaterialTheme.typography.bodySmall,
                        color = AITheme.TextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(bookmarks) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        onClick = { onBookmarkClick(bookmark.url) },
                        onDelete = { /* Delete */ }
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = AITheme.AccentPurple
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AITheme.TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = bookmark.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = AITheme.TextSecondary,
                    maxLines = 1
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp),
                    tint = AITheme.TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AITheme.TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Downloads",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AITheme.TextPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = AITheme.TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No downloads yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AITheme.TextSecondary
                )
                Text(
                    text = "Files you download will appear here",
                    style = MaterialTheme.typography.bodySmall,
                    color = AITheme.TextTertiary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AITheme.TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AITheme.TextPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AITheme.DarkBackground)
        ) {
            // Profile header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(AITheme.AccentPurple, AITheme.AccentBlue)
                                ),
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.username?.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = currentUser?.username ?: "User",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AITheme.TextPrimary
                    )
                    
                    Text(
                        text = currentUser?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AITheme.TextSecondary
                    )
                    
                    if (currentUser?.isVip == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AITheme.AccentOrange.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AITheme.AccentOrange
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VIP Member",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AITheme.AccentOrange
                                )
                            }
                        }
                    }
                }
            }
            
            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "${currentUser?.points ?: 0}", label = "Points")
                StatItem(value = "0", label = "Invites")
                StatItem(value = "0", label = "Days")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Menu items
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Upgrade to VIP",
                subtitle = "Unlock all features",
                onClick = { }
            )
            
            SettingsItem(
                icon = Icons.Default.CardGiftcard,
                title = "Invite Friends",
                onClick = { }
            )
            
            SettingsItem(
                icon = Icons.Default.History,
                title = "Activity",
                onClick = { }
            )
            
            SettingsItem(
                icon = Icons.Default.Sync,
                title = "Sync Data",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Logout button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AITheme.AccentRed
                    ),
                    border = BorderStroke(1.dp, AITheme.AccentRed)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AITheme.TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AITheme.TextSecondary
        )
    }
}
