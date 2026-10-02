package ir.bordermanager.data

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("border_manager_settings", Context.MODE_PRIVATE)

    var themeMode: String
        get() = prefs.getString("theme_mode", "LIGHT") ?: "LIGHT"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    var companyName: String
        get() = prefs.getString("company_name", "بازرگانی") ?: "بازرگانی"
        set(value) = prefs.edit().putString("company_name", value).apply()

    var companyPhone: String
        get() = prefs.getString("company_phone", "") ?: ""
        set(value) = prefs.edit().putString("company_phone", value).apply()

    var companyAddress: String
        get() = prefs.getString("company_address", "") ?: ""
        set(value) = prefs.edit().putString("company_address", value).apply()

    var compactMode: Boolean
        get() = prefs.getBoolean("compact_mode", false)
        set(value) = prefs.edit().putBoolean("compact_mode", value).apply()
}
