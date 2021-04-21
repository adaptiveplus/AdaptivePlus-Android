package plus.adaptive.qaapp.utils

import android.content.Context


class AppSharedPreferences(context: Context) {

    companion object {
        private const val PREF_APP = "adaptive_plus_app_preferences"

        const val ADAPTIVE_ENVS = "adaptive_envs"
        const val ADAPTIVE_CUSTOM_IPS = "adaptive_custom_ips"
    }


    private val preferences = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)


    fun getString(key: String): String? = preferences.getString(key, null)

    fun saveString(key: String, value: String) = with (preferences.edit()) {
        putString(key, value)
        commit()
    }

    fun getInt(key: String): Int = preferences.getInt(key, -1)

    fun saveInt(key: String, value: Int) = with (preferences.edit()) {
        putInt(key, value)
        commit()
    }

    fun getLong(key: String): Long = preferences.getLong(key, -1)

    fun saveLong(key: String, value: Long) = with (preferences.edit()) {
        putLong(key, value)
        commit()
    }

    fun getFloat(key: String): Float = preferences.getFloat(key, -1f)

    fun saveFloat(key: String, value: Float) = with (preferences.edit()) {
        putFloat(key, value)
        commit()
    }

    fun getBoolean(key: String): Boolean = preferences.getBoolean(key, false)

    fun saveBoolean(key: String, value: Boolean) = with (preferences.edit()) {
        putBoolean(key, value)
        commit()
    }

    fun contains(key: String) = preferences.contains(key)

    fun remove(key: String) = with (preferences.edit()) {
        remove(key)
        commit()
    }
}