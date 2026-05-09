package com.aibrowser.app.browser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.webkit.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIBrowserCore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _browserState = MutableStateFlow<BrowserState>(BrowserState.Idle)
    val browserState: StateFlow<BrowserState> = _browserState.asStateFlow()
    
    private val _currentUrl = MutableStateFlow("")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()
    
    private val _currentTitle = MutableStateFlow("")
    val currentTitle: StateFlow<String> = _currentTitle.asStateFlow()
    
    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()
    
    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress.asStateFlow()
    
    private var webView: WebView? = null
    private var client: AIBrowserClient? = null
    
    var isIncognito: Boolean = false
    var isAdBlockEnabled: Boolean = true
    var customUserAgent: String = ""
    
    @SuppressLint("SetJavaScriptEnabled")
    fun initializeWebView(webView: WebView) {
        this.webView = webView
        
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = if (isIncognito) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
                
                // Security settings
                allowFileAccess = false
                allowContentAccess = false
                setGeolocationEnabled(false)
                
                // Performance settings
                loadsImagesAutomatically = true
                mediaPlaybackRequiresUserGesture = false
                
                // User agent
                if (customUserAgent.isNotEmpty()) {
                    userAgentString = customUserAgent
                }
                
                // Viewport
                useWideViewPort = true
                loadWithOverviewMode = true
                
                // Zoom
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                
                // Hardware acceleration
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = true
                }
                
                // Mixed content mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                }
            }
            
            // WebView client
            client = AIBrowserClient(
                onPageStarted = { url ->
                    _currentUrl.value = url
                    _browserState.value = BrowserState.Loading(url)
                },
                onPageFinished = { url, title ->
                    _currentUrl.value = url
                    _currentTitle.value = title
                    _isLoading.value = false
                    _browserState.value = BrowserState.Ready(url, title)
                },
                onReceivedError = { error ->
                    _browserState.value = BrowserState.Error(error)
                },
                onReceivedTitle = { title ->
                    _currentTitle.value = title
                },
                shouldInterceptRequest = if (isAdBlockEnabled) {
                    { request -> adBlockInterceptor(request) }
                } else null
            )
            webViewClient = client
            
            // WebChrome client for progress
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    _loadingProgress.value = newProgress
                    if (newProgress == 100) {
                        _isLoading.value = false
                    }
                }
                
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    _currentTitle.value = title ?: ""
                }
            }
        }
        
        _browserState.value = BrowserState.Ready("", "")
    }
    
    fun loadUrl(url: String) {
        val processedUrl = preprocessUrl(url)
        webView?.loadUrl(processedUrl)
    }
    
    fun loadHtml(html: String, baseUrl: String = "about:blank") {
        webView?.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
    }
    
    private fun preprocessUrl(input: String): String {
        return when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            input.contains(".") && !input.contains(" ") -> "https://$input"
            else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(input, "UTF-8")}"
        }
    }
    
    fun goBack() {
        if (_canGoBack.value) {
            webView?.goBack()
        }
    }
    
    fun goForward() {
        if (_canGoForward.value) {
            webView?.goForward()
        }
    }
    
    fun reload() {
        webView?.reload()
    }
    
    fun stopLoading() {
        webView?.stopLoading()
    }
    
    fun destroy() {
        webView?.destroy()
        webView = null
        client = null
    }
    
    private fun adBlockInterceptor(request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()
        
        // Check against ad block rules
        for (rule in AdBlockRules.rules) {
            if (url.contains(rule, ignoreCase = true)) {
                return WebResourceResponse(
                    "text/plain",
                    "UTF-8",
                    null
                )
            }
        }
        
        return null
    }
    
    fun updateCanGoState() {
        webView?.let {
            _canGoBack.value = it.canGoBack()
            _canGoForward.value = it.canGoForward()
        }
    }
    
    fun captureSnapshot(): Bitmap? {
        return webView?.capturePicture()?.toBitmap()
    }
}

class AIBrowserClient(
    private val onPageStarted: (String) -> Unit,
    private val onPageFinished: (String, String) -> Unit,
    private val onReceivedError: (WebViewError) -> Unit,
    private val onReceivedTitle: (String) -> Unit,
    private val shouldInterceptRequest: ((WebResourceRequest) -> WebResourceResponse?)? = null
) : WebViewClient() {
    
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        url?.let { onPageStarted(it) }
    }
    
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        url?.let { onPageFinished(it, view?.title ?: "") }
    }
    
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            error?.let {
                if (request?.isForMainFrame == true) {
                    onReceivedError(
                        WebViewError(
                            errorCode = it.errorCode,
                            description = it.description.toString(),
                            failingUrl = request.url.toString()
                        )
                    )
                }
            }
        }
    }
    
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return request?.let { shouldInterceptRequest?.invoke(it) }
    }
    
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return false
    }
}

data class WebViewError(
    val errorCode: Int,
    val description: String,
    val failingUrl: String
)

sealed class BrowserState {
    object Idle : BrowserState()
    data class Loading(val url: String) : BrowserState()
    data class Ready(val url: String, val title: String) : BrowserState()
    data class Error(val error: WebViewError) : BrowserState()
}

object AdBlockRules {
    val rules = listOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "google-analytics.com",
        "googletagmanager.com",
        "googletagservices.com",
        "facebook.net",
        "facebook.com/tr",
        "analytics.facebook.com",
        "adnxs.com",
        "adsrvr.org",
        "criteo.com",
        "criteo.net",
        "outbrain.com",
        "taboola.com",
        "mgid.com",
        "revcontent.com",
        "popads.net",
        "popcash.net",
        "adf.ly",
        "pu.sh",
        "sh.st",
        "bc.vc",
        "linkshrink.net",
        "ad.me",
        "ads.",
        "banner",
        "sponsor",
        "tracking",
        "pixel",
        "beacon"
    )
}
