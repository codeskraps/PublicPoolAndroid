package com.codeskraps.publicpool.data.remote

import android.content.Context
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UmamiAnalyticsDataSource(private val context: Context) {
    
    private val webView: WebView by lazy {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = UmamiWebViewClient()
            loadUrl("about:blank")
        }
    }
    
    private val umamiScript = """
        <script defer src="https://umami.codeskraps.com/script.js" data-website-id="b3e6309f-9724-48e5-a1c6-11757de3fe83"></script>
    """.trimIndent()
    
    suspend fun initialize() = withContext(Dispatchers.Main) {
        webView.loadDataWithBaseURL(
            "https://umami.codeskraps.com",
            "<html><head>$umamiScript</head><body></body></html>",
            "text/html",
            "UTF-8",
            null
        )
    }
    
    suspend fun trackPageView(pageName: String) = withContext(Dispatchers.Main) {
        webView.evaluateJavascript(
            "umami.trackView('$pageName')",
            null
        )
    }
    
    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap()) = withContext(Dispatchers.Main) {
        val dataJson = eventData.entries.joinToString(",") { 
            "\"${it.key}\": \"${it.value}\"" 
        }
        webView.evaluateJavascript(
            "umami.trackEvent('$eventName', {$dataJson})",
            null
        )
    }
    
    private class UmamiWebViewClient : WebViewClientCompat()
} 