package ohior.app.askbox.service.database

import android.content.Context
import android.content.SharedPreferences

object PrefManager {
    private lateinit var sharedPreferences: SharedPreferences
    private const val PREF_NAME = "MyPrefs"
    const val BOT_RESULT_KEY = "bot_result_key"

    fun initialize(context: Context) {
        sharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun removeData(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }


    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }


    fun containsKey(key: String): Boolean {
        return sharedPreferences.contains(key)
    }


}