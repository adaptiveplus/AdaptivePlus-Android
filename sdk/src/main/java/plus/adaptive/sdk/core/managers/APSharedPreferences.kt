package plus.adaptive.sdk.core.managers

import android.content.Context
import plus.adaptive.sdk.data.ENV_NAME


internal class APSharedPreferences(context: Context) {

    companion object {
        private const val PREF_ADAPTIVE_PLUS = "adaptive_plus_sdk_preferences"

        const val AUTH_TOKEN = "auth_token"
        const val AUTH_TOKEN_EXPIRATION_DATE = "auth_token_expiration_date"
        const val AP_USER_ID = "ap_user_id"
        const val EXTERNAL_USER_ID = "external_user_id"
        const val IS_CAMPAIGN_WATCHED = "is_campaign_watched"
        const val IS_EVENT_TRACKING_DISABLED = "is_event_tracking_disabled"
        const val CAMPAIGN_WATCHED_COUNT = "campaign_watched_count"
        const val POLL_CHOSEN_ANSWER_ID = "poll_chosen_answer_id"
        const val POLL_DATA = "poll_data"
    }


    private val preferences = context.getSharedPreferences(PREF_ADAPTIVE_PLUS, Context.MODE_PRIVATE)


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

    fun remove(key: String) = with (preferences.edit()) {
        remove(key)
        commit()
    }

    fun getWatchedStoryIds(userId: String): MutableSet<String>? = preferences.getStringSet(userId, null)

    fun saveWatchedStoryId(userId: String, value: String) {
        val watchedSet = getWatchedStoryIds(userId) ?: HashSet<String>()
        if(!(watchedSet as HashSet<String>).contains(value)){
            saveWatchedStoryCount(value)
        }
        watchedSet.add(value)
        preferences.edit().putStringSet(userId, watchedSet).apply()
    }

    fun getWatchedStoryCount(storyId: String): Int = preferences.getInt("$ENV_NAME story $storyId", 0)

    fun saveWatchedStoryCount(storyId: String) {
        val count = getWatchedStoryCount(storyId)
        preferences.edit().putInt("$ENV_NAME story $storyId", count+1).apply()
    }

    fun getWatchedBannerCount(bannerId: String): Int = preferences.getInt("$ENV_NAME banner $bannerId", 0)

    fun saveWatchedBannerIdCount(bannerId: String) {
        val count = getWatchedBannerCount(bannerId)
        preferences.edit().putInt("$ENV_NAME banner $bannerId", count+1).apply()
    }

    fun getWatchedInstructionCount(instructionId: String): Int =
        preferences.getInt("$ENV_NAME instruction $instructionId", 0)

    fun saveWatchedInstructionCount(instructionId: String) {
        val count = getWatchedInstructionCount(instructionId)
        preferences.edit().putInt("$ENV_NAME instruction $instructionId", count+1).apply()
    }
}