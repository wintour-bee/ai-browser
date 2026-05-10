package com.aibrowser.app.presentation.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.aibrowser.app.browser.AIBrowserCore
import com.aibrowser.app.presentation.ui.components.*
import com.aibrowser.app.presentation.ui.theme.AITheme
import com.aibrowser.app.presentation.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    onNavigateBack: () -> Unit,
    viewModel: BrowserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Use the injected viewModel directly instead of creating a new one
    val browserViewModel = viewModel
    
    val browserState by viewModel.browserState.collectAsState()
    val currentUrl by viewModel.currentUrl.collectAsState()
    val currentTitle by viewModel.currentTitle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loadingProgress by viewModel.loadingProgress.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val canGoForward by viewModel.canGoForward.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val showUrlBar by viewModel.showUrlBar.collectAsState()
    val showTabSwitcher by viewModel.showTabSwitcher.collectAsState()
    val isIncognito by viewModel.isIncognito.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // URL Bar
            AnimatedVisibility(
                visible = showUrlBar,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AITheme.DarkSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleUrlBar() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = if (canGoBack) AITheme.TextPrimary else AITheme.TextSecondary
                            )
                        }
                        
                        SearchBar(
                            query = searchQuery.ifEmpty { currentUrl },
                            onQueryChange = { searchQuery = it },
                            onSearch = {
                                viewModel.loadUrl(searchQuery)
                                viewModel.toggleUrlBar()
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = "Search or enter URL"
                        )
                        
                        IconButton(onClick = { viewModel.toggleTabSwitcher() }) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Tabs",
                                tint = AITheme.TextPrimary
                            )
                        }
                    }
                }
            }
            
            // WebView
            Box(modifier = Modifier.weight(1f)) {
                AIBrowserWebView(
                    url = currentUrl,
                    onUrlChange = { },
                    isIncognito = isIncognito,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Loading indicator
                androidx.compose.animation.AnimatedVisibility(
                    visible = isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LinearProgressIndicator(
                        progress = loadingProgress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = AITheme.AccentPurple,
                        trackColor = AITheme.DarkSurfaceVariant
                    )
                }
            }
            
            // Bottom toolbar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        enabled = canGoBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (canGoBack) AITheme.TextPrimary else AITheme.TextSecondary
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.goForward() },
                        enabled = canGoForward
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (canGoForward) AITheme.TextPrimary else AITheme.TextSecondary
                        )
                    }
                    
                    IconButton(onClick = { viewModel.toggleUrlBar() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AITheme.TextPrimary
                        )
                    }
                    
                    IconButton(onClick = { viewModel.toggleTabSwitcher() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Tab",
                            tint = AITheme.TextPrimary
                        )
                    }
                    
                    IconButton(onClick = { viewModel.toggleMenu() }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = AITheme.TextPrimary
                        )
                    }
                }
            }
        }
        
        // Tab Switcher Overlay
        AnimatedVisibility(
            visible = showTabSwitcher,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            TabSwitcherOverlay(
                tabs = tabs,
                onTabSelect = { viewModel.selectTab(it) },
                onTabClose = { viewModel.closeTab(it) },
                onNewTab = { viewModel.createNewTab() },
                onDismiss = { viewModel.toggleTabSwitcher() }
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AIBrowserWebView(
    url: String,
    onUrlChange: (String) -> Unit,
    isIncognito: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    cacheMode = if (isIncognito) {
                        android.webkit.WebSettings.LOAD_NO_CACHE
                    } else {
                        android.webkit.WebSettings.LOAD_DEFAULT
                    }
                    allowFileAccess = false
                    allowContentAccess = false
                    loadsImagesAutomatically = true
                    mediaPlaybackRequiresUserGesture = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                
                if (url.isNotEmpty() && url != "about:blank") {
                    loadUrl(url)
                }
            }
        },
        update = { webView ->
            if (url.isNotEmpty() && url != webView.url) {
                webView.loadUrl(url)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSwitcherOverlay(
    tabs: List<com.aibrowser.app.domain.model.Tab>,
    onTabSelect: (String) -> Unit,
    onTabClose: (String) -> Unit,
    onNewTab: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AITheme.DarkBackground.copy(alpha = 0.95f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tabs (${tabs.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AITheme.TextPrimary
                )
                
                Row {
                    IconButton(onClick = onNewTab) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Tab",
                            tint = AITheme.AccentPurple
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AITheme.TextPrimary
                        )
                    }
                }
            }
            
            // Tabs grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tabs) { tab ->
                    TabPreviewCard(
                        tab = tab,
                        onClick = { onTabSelect(tab.id) },
                        onClose = { onTabClose(tab.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabPreviewCard(
    tab: com.aibrowser.app.domain.model.Tab,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f),
        colors = CardDefaults.cardColors(
            containerColor = AITheme.DarkSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Tab header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AITheme.DarkSurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AITheme.TextSecondary
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp),
                        tint = AITheme.TextSecondary
                    )
                }
            }
            
            // Content preview
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AITheme.TextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tab.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = AITheme.TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
