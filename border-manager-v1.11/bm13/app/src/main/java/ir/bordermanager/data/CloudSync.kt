package ir.bordermanager.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

private const val SUPABASE_URL = "https://gksyxajdhgjqxcgeeacd.supabase.co"
private const val SUPABASE_KEY = "sb_publishable_q_RAPYVoMB0C_8AkJ35FZQ_eDo-D6fu"

class CloudSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("border_manager_cloud", Context.MODE_PRIVATE)

    var email: String
        get() = prefs.getString("email", "") ?: ""
        set(value) = prefs.edit().putString("email", value).apply()
    var accessToken: String
        get() = prefs.getString("access_token", "") ?: ""
        set(value) = prefs.edit().putString("access_token", value).apply()
    var refreshToken: String
        get() = prefs.getString("refresh_token", "") ?: ""
        set(value) = prefs.edit().putString("refresh_token", value).apply()
    var userId: String
        get() = prefs.getString("user_id", "") ?: ""
        set(value) = prefs.edit().putString("user_id", value).apply()
    var expiresAt: Long
        get() = prefs.getLong("expires_at", 0L)
        set(value) = prefs.edit().putLong("expires_at", value).apply()
    var dirty: Boolean
        get() = prefs.getBoolean("dirty", false)
        set(value) = prefs.edit().putBoolean("dirty", value).apply()
    var lastSyncAt: Long
        get() = prefs.getLong("last_sync_at", 0L)
        set(value) = prefs.edit().putLong("last_sync_at", value).apply()

    val loggedIn: Boolean get() = userId.isNotBlank() && accessToken.isNotBlank()

    fun saveAuth(json: JSONObject, fallbackEmail: String = email) {
        val user = json.optJSONObject("user")
        val newAccess = json.optString("access_token")
        val newRefresh = json.optString("refresh_token")
        if (newAccess.isNotBlank()) accessToken = newAccess
        if (newRefresh.isNotBlank()) refreshToken = newRefresh
        val uid = user?.optString("id").orEmpty()
        if (uid.isNotBlank()) userId = uid
        val mail = user?.optString("email").orEmpty().ifBlank { fallbackEmail }
        if (mail.isNotBlank()) email = mail
        val expiresIn = json.optLong("expires_in", 3600L)
        expiresAt = System.currentTimeMillis() + expiresIn * 1000L
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}

data class AuthResult(val signedIn: Boolean, val message: String)
data class SyncResult(val ok: Boolean, val message: String, val restored: Boolean = false)

class CloudSyncManager(context: Context, private val db: DatabaseHelper) {
    val session = CloudSessionStore(context.applicationContext)
    private val executor = Executors.newSingleThreadExecutor()

    fun signIn(email: String, password: String): AuthResult {
        val body = JSONObject().put("email", email.trim()).put("password", password)
        val result = request("POST", "/auth/v1/token?grant_type=password", body = body, authenticated = false)
        session.saveAuth(result, email.trim())
        return AuthResult(true, "ورود با موفقیت انجام شد")
    }

    fun signUp(email: String, password: String): AuthResult {
        val body = JSONObject().put("email", email.trim()).put("password", password)
        val result = request("POST", "/auth/v1/signup", body = body, authenticated = false)
        val token = result.optString("access_token")
        return if (token.isNotBlank()) {
            session.saveAuth(result, email.trim())
            AuthResult(true, "حساب ساخته شد و وارد شدی")
        } else {
            AuthResult(false, "حساب ساخته شد. ایمیل تأیید Supabase را باز کن و بعد وارد شو.")
        }
    }

    fun bootstrap(): SyncResult {
        if (!session.loggedIn) return SyncResult(false, "وارد حساب نشده‌ای")
        return try {
            if (session.dirty) {
                uploadCurrentBackup()
                SyncResult(true, "اطلاعات گوشی روی حساب ذخیره شد")
            } else {
                val remote = downloadBackup()
                if (remote != null) {
                    db.importBackup(remote)
                    session.lastSyncAt = System.currentTimeMillis()
                    SyncResult(true, "اطلاعات حساب بازیابی شد", restored = true)
                } else {
                    uploadCurrentBackup()
                    SyncResult(true, "اولین نسخه اطلاعات روی حساب ذخیره شد")
                }
            }
        } catch (t: Throwable) {
            SyncResult(false, friendlyError(t))
        }
    }

    fun syncNow(): SyncResult {
        if (!session.loggedIn) return SyncResult(false, "وارد حساب نشده‌ای")
        return try {
            uploadCurrentBackup()
            SyncResult(true, "همگام‌سازی با حساب انجام شد")
        } catch (t: Throwable) {
            SyncResult(false, friendlyError(t))
        }
    }

    fun scheduleBackup() {
        // Mark the local database dirty even while logged out. Otherwise a user
        // who records trucks offline would lose those changes when they later
        // sign in and bootstrap pulls the remote backup.
        session.dirty = true
        if (!session.loggedIn) return
        executor.execute {
            try { uploadCurrentBackup() } catch (_: Throwable) { session.dirty = true }
        }
    }

    fun logout() {
        if (session.loggedIn) {
            try { request("POST", "/auth/v1/logout", authenticated = true) } catch (_: Throwable) { }
        }
        session.clear()
    }

    private fun uploadCurrentBackup() {
        ensureFreshToken()
        val payload = db.exportBackup()
        val body = JSONObject()
            .put("user_id", session.userId)
            .put("payload", payload)
            .put("schema_version", 5)
            .put("updated_at", java.time.Instant.now().toString())
        request(
            method = "POST",
            path = "/rest/v1/user_backups?on_conflict=user_id",
            body = body,
            authenticated = true,
            extraHeaders = mapOf("Prefer" to "resolution=merge-duplicates,return=minimal")
        )
        session.dirty = false
        session.lastSyncAt = System.currentTimeMillis()
    }

    private fun downloadBackup(): JSONObject? {
        ensureFreshToken()
        val raw = requestRaw(
            method = "GET",
            path = "/rest/v1/user_backups?user_id=eq.${session.userId}&select=payload,schema_version,updated_at",
            authenticated = true
        )
        val arr = JSONArray(raw)
        if (arr.length() == 0) return null
        return arr.getJSONObject(0).optJSONObject("payload")
    }

    private fun ensureFreshToken() {
        if (!session.loggedIn) throw IllegalStateException("وارد حساب نشده‌ای")
        if (System.currentTimeMillis() < session.expiresAt - 60_000L) return
        if (session.refreshToken.isBlank()) throw IllegalStateException("نشست حساب منقضی شده؛ دوباره وارد شو")
        val body = JSONObject().put("refresh_token", session.refreshToken)
        val result = request("POST", "/auth/v1/token?grant_type=refresh_token", body = body, authenticated = false)
        session.saveAuth(result)
    }

    private fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        authenticated: Boolean,
        extraHeaders: Map<String, String> = emptyMap()
    ): JSONObject {
        val raw = requestRaw(method, path, body, authenticated, extraHeaders)
        return if (raw.isBlank()) JSONObject() else JSONObject(raw)
    }

    private fun requestRaw(
        method: String,
        path: String,
        body: JSONObject? = null,
        authenticated: Boolean,
        extraHeaders: Map<String, String> = emptyMap()
    ): String {
        val conn = (URL(SUPABASE_URL + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 12_000
            readTimeout = 15_000
            setRequestProperty("apikey", SUPABASE_KEY)
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            if (authenticated) setRequestProperty("Authorization", "Bearer ${session.accessToken}")
            extraHeaders.forEach { (k, v) -> setRequestProperty(k, v) }
            if (body != null) {
                doOutput = true
                outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            }
        }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } }.orEmpty()
        conn.disconnect()
        if (code !in 200..299) {
            val message = try {
                val j = JSONObject(text)
                j.optString("msg").ifBlank { j.optString("message") }.ifBlank { j.optString("error_description") }.ifBlank { text }
            } catch (_: Throwable) { text }
            throw IllegalStateException(message.ifBlank { "خطای ارتباط با حساب ($code)" })
        }
        return text
    }

    private fun friendlyError(t: Throwable): String {
        val m = t.message.orEmpty()
        return when {
            m.contains("Invalid login credentials", true) -> "ایمیل یا رمز عبور اشتباه است"
            m.contains("Email not confirmed", true) -> "اول ایمیلت را تأیید کن، بعد وارد شو"
            m.contains("already registered", true) -> "این ایمیل قبلاً ثبت شده؛ از ورود استفاده کن"
            m.contains("network", true) || m.contains("Unable to resolve host", true) -> "اینترنت در دسترس نیست؛ اطلاعات روی گوشی محفوظ می‌ماند"
            else -> m.ifBlank { "ارتباط با حساب انجام نشد" }
        }
    }
}
