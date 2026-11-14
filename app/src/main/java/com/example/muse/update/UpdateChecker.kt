package com.example.muse.update

import android.content.Context
import android.content.SharedPreferences
import com.example.muse.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionName: String,
    val releaseName: String,
    val releaseNotes: String,
    val apkUrl: String?,
    val releasePageUrl: String
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val info: UpdateInfo) : UpdateCheckResult()
    data object UpToDate : UpdateCheckResult()
    data object NetworkError : UpdateCheckResult()
}

object UpdateChecker {

    private const val GITHUB_OWNER = "Andrey3141"
    private const val GITHUB_REPO = "MUSE"
    private const val PREFS_NAME = "muse_update_prefs"
    private const val KEY_LAST_CHECK = "last_check_ms"
    private const val CHECK_INTERVAL_MS = 24L * 60 * 60 * 1000

    fun shouldCheck(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val last = prefs.getLong(KEY_LAST_CHECK, 0L)
        return System.currentTimeMillis() - last >= CHECK_INTERVAL_MS
    }

    private fun markChecked(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_CHECK, System.currentTimeMillis())
            .apply()
    }

    suspend fun checkForUpdate(context: Context, force: Boolean = false): UpdateCheckResult =
        withContext(Dispatchers.IO) {
            if (!force && !shouldCheck(context)) {
                return@withContext UpdateCheckResult.UpToDate
            }

            val url = URL("https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "MUSE-Android")
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000

            try {
                val code = connection.responseCode
                if (code != 200) {
                    return@withContext UpdateCheckResult.NetworkError
                }

                val body = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)

                val tag = json.optString("tag_name").removePrefix("v")
                val releaseName = json.optString("name", "MUSE $tag")
                val releaseNotes = json.optString("body", "")
                val htmlUrl = json.optString("html_url", "")

                val assets = json.optJSONArray("assets")
                var apkUrl: String? = null
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            apkUrl = asset.optString("browser_download_url").takeIf { it.isNotBlank() }
                            break
                        }
                    }
                }

                markChecked(context)

                val current = BuildConfig.VERSION_NAME
                if (isNewer(tag, current)) {
                    UpdateCheckResult.UpdateAvailable(
                        UpdateInfo(
                            versionName = tag,
                            releaseName = releaseName,
                            releaseNotes = releaseNotes,
                            apkUrl = apkUrl,
                            releasePageUrl = htmlUrl
                        )
                    )
                } else {
                    UpdateCheckResult.UpToDate
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UpdateCheckResult.NetworkError
            } finally {
                connection.disconnect()
            }
        }

    private fun isNewer(remote: String, current: String): Boolean {
        fun parse(v: String): List<Int> =
            v.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
        val r = parse(remote)
        val c = parse(current)
        for (i in 0 until maxOf(r.size, c.size)) {
            val a = r.getOrElse(i) { 0 }
            val b = c.getOrElse(i) { 0 }
            if (a > b) return true
            if (a < b) return false
        }
        return false
    }
}