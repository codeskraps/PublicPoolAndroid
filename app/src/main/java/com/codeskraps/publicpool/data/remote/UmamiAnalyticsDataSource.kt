package com.codeskraps.publicpool.data.remote

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import com.codeskraps.publicpool.di.AppReadinessState
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Locale

data class UmamiConfig(
    val websiteId: String,
    val baseUrl: String
)

class UmamiAnalyticsDataSource(
    private val context: Context,
    private val appReadinessState: AppReadinessState,
    private val config: UmamiConfig,
    private val client: HttpClient
) {

    private val sendUrl = "${config.baseUrl}/api/send"

    private val screenResolution: String by lazy {
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        "${metrics.widthPixels}x${metrics.heightPixels}"
    }

    private val language: String by lazy {
        Locale.getDefault().toLanguageTag()
    }

    private val userAgent: String by lazy {
        val appVersion = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
        } catch (_: Exception) {
            "unknown"
        }
        "PublicPool/$appVersion (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})"
    }

    suspend fun initialize() {
        // No setup needed for direct API calls — mark app as ready immediately
        appReadinessState.setReady()
    }

    suspend fun trackPageView(pageName: String) {
        val path = if (pageName.startsWith("/")) pageName else "/$pageName"
        val title = pageName
            .replace("-", " ")
            .replaceFirstChar { it.uppercase() }

        val payload = buildMap {
            put("website", JsonPrimitive(config.websiteId))
            put("hostname", JsonPrimitive("app.publicpool"))
            put("screen", JsonPrimitive(screenResolution))
            put("language", JsonPrimitive(language))
            put("title", JsonPrimitive(title))
            put("url", JsonPrimitive(path))
            put("referrer", JsonPrimitive(""))
        }

        sendEvent("event", JsonObject(payload))
    }

    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap()) {
        val payload = buildMap {
            put("website", JsonPrimitive(config.websiteId))
            put("hostname", JsonPrimitive("app.publicpool"))
            put("screen", JsonPrimitive(screenResolution))
            put("language", JsonPrimitive(language))
            put("url", JsonPrimitive("/"))
            put("referrer", JsonPrimitive(""))
            put("name", JsonPrimitive(eventName))
            if (eventData.isNotEmpty()) {
                put("data", JsonObject(eventData.mapValues { JsonPrimitive(it.value) }))
            }
        }

        sendEvent("event", JsonObject(payload))
    }

    suspend fun identifyUser(walletAddress: String?) {
        if (walletAddress.isNullOrBlank()) return

        val anonymizedId = if (walletAddress.length > 8) {
            "${walletAddress.take(4)}...${walletAddress.takeLast(4)}"
        } else {
            walletAddress
        }

        val payload = buildMap {
            put("website", JsonPrimitive(config.websiteId))
            put("hostname", JsonPrimitive("app.publicpool"))
            put("screen", JsonPrimitive(screenResolution))
            put("language", JsonPrimitive(language))
            put("url", JsonPrimitive("/"))
            put("referrer", JsonPrimitive(""))
            put("data", JsonObject(mapOf("wallet_id" to JsonPrimitive(anonymizedId))))
        }

        sendEvent("identify", JsonObject(payload))
    }

    private suspend fun sendEvent(type: String, payload: JsonObject) {
        try {
            val body = JsonObject(
                mapOf(
                    "type" to JsonPrimitive(type),
                    "payload" to payload
                )
            )
            client.post(sendUrl) {
                contentType(ContentType.Application.Json)
                header("User-Agent", userAgent)
                setBody(body.toString())
            }
        } catch (_: Exception) {
            // Silently ignore analytics failures
        }
    }
}
