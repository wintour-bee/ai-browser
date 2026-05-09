package com.aibrowser.app.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aibrowser.app.domain.model.*
import com.aibrowser.app.presentation.ui.components.*
import com.aibrowser.app.presentation.ui.theme.AITheme
import com.aibrowser.app.presentation.viewmodel.AIChatViewModel
import com.aibrowser.app.ai.ChatState
import com.aibrowser.app.ai.AIProviderConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val chatState by viewModel.chatState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    
    var showProviderSelector by remember { mutableStateOf(false) }
    
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
                            text = "AI Chat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AITheme.TextPrimary
                        )
                    }
                    
                    Row {
                        // Provider selector
                        Box {
                            IconButton(onClick = { showProviderSelector = true }) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Select AI",
                                    tint = AITheme.AccentPurple
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showProviderSelector,
                                onDismissRequest = { showProviderSelector = false }
                            ) {
                                AIProvider.entries.forEach { provider ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = provider.name,
                                                fontWeight = if (provider == selectedProvider) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectProvider(provider)
                                            showProviderSelector = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Psychology,
                                                contentDescription = null,
                                                tint = if (provider == selectedProvider) AITheme.AccentPurple else AITheme.TextSecondary
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        
                        IconButton(onClick = { viewModel.startNewChat() }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Chat",
                                tint = AITheme.TextPrimary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AITheme.DarkSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Current AI indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AITheme.AccentPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${selectedProvider.name} • ${selectedModel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AITheme.AccentPurple,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Input field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { viewModel.updateInputText(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask AI anything...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AITheme.AccentPurple,
                                unfocusedBorderColor = AITheme.BorderDark,
                                focusedTextColor = AITheme.TextPrimary,
                                unfocusedTextColor = AITheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(AITheme.AccentPurple, AITheme.AccentBlue)
                                    ),
                                    shape = CircleShape
                                ),
                            enabled = inputText.isNotBlank() && chatState !is ChatState.Sending
                        ) {
                            if (chatState is ChatState.Sending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    }
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
            if (messages.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(AITheme.AccentPurple, AITheme.AccentBlue)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "AI Assistant",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AITheme.TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Ask anything - from quick questions to complex reasoning. ${selectedProvider.name} is here to help.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AITheme.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Suggested prompts
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Summarize this article for me",
                                "Help me write a professional email",
                                "Explain this concept in simple terms",
                                "Translate this to Chinese"
                            ).forEach { prompt ->
                                Surface(
                                    onClick = { viewModel.updateInputText(prompt) },
                                    color = AITheme.DarkSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, AITheme.BorderDark)
                                ) {
                                    Text(
                                        text = prompt,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AITheme.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Messages list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            isStreaming = message.isStreaming
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: AIMessage,
    isStreaming: Boolean
) {
    val isUser = message.role == AIRole.USER
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(AITheme.AccentPurple, AITheme.AccentBlue)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            color = if (isUser) AITheme.AccentPurple else AITheme.DarkSurface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else AITheme.TextPrimary
                )
                
                if (isStreaming) {
                    Spacer(modifier = Modifier.height(4.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = AITheme.AccentPurple
                    )
                }
            }
        }
        
        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AITheme.DarkSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = AITheme.TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VPNScreen(
    onNavigateBack: () -> Unit,
    viewModel: com.aibrowser.app.presentation.viewmodel.VPNViewModel = hiltViewModel()
) {
    val servers by viewModel.servers.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val connectionInfo by viewModel.connectionInfo.collectAsState()
    
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
                        text = "VPN",
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
            // Connection status card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isConnected) AITheme.AccentGreen.copy(alpha = 0.1f) else AITheme.DarkSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isConnected) AITheme.AccentGreen else AITheme.BorderDark
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Status indicator
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (isConnected) AITheme.AccentGreen else AITheme.AccentPurple,
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        color = if (isConnected) AITheme.AccentGreen else AITheme.AccentPurple,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isConnecting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (isConnected) Icons.Default.Shield else Icons.Default.VpnKey,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (isConnected) "Connected" else if (isConnecting) "Connecting..." else "Disconnected",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) AITheme.AccentGreen else AITheme.TextPrimary
                        )
                        
                        if (selectedServer != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedServer!!.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AITheme.TextSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Connect/Disconnect button
                        GradientButton(
                            text = if (isConnected) "Disconnect" else "Connect",
                            onClick = {
                                if (isConnected) {
                                    viewModel.disconnect()
                                } else if (selectedServer != null) {
                                    viewModel.connect(selectedServer!!)
                                } else if (servers.isNotEmpty()) {
                                    viewModel.connect(servers.first())
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = if (isConnected) Icons.Default.Stop else Icons.Default.PlayArrow,
                            enabled = !isConnecting
                        )
                    }
                }
            }
            
            // Server list
            SectionHeader(title = "Server List")
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(servers) { server ->
                    ServerItem(
                        server = server,
                        isSelected = selectedServer?.id == server.id,
                        onClick = { viewModel.connect(server) },
                        onPing = { viewModel.pingServer(server) }
                    )
                }
            }
        }
    }
}

@Composable
fun ServerItem(
    server: VPNServer,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPing: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) AITheme.AccentPurple.copy(alpha = 0.1f) else AITheme.DarkSurface,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(1.dp, AITheme.AccentPurple) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country flag placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AITheme.DarkSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = server.country.take(2).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AITheme.TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = AITheme.TextPrimary
                )
                Text(
                    text = "${server.city} • ${server.protocol.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AITheme.TextSecondary
                )
            }
            
            // Ping indicator
            if (server.pingLatency != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        server.pingLatency!! < 50 -> AITheme.AccentGreen.copy(alpha = 0.2f)
                        server.pingLatency!! < 100 -> AITheme.AccentOrange.copy(alpha = 0.2f)
                        else -> AITheme.AccentRed.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = "${server.pingLatency}ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            server.pingLatency!! < 50 -> AITheme.AccentGreen
                            server.pingLatency!! < 100 -> AITheme.AccentOrange
                            else -> AITheme.AccentRed
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (server.isPremium) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Premium",
                    modifier = Modifier.size(16.dp),
                    tint = AITheme.AccentOrange
                )
            }
        }
    }
}
