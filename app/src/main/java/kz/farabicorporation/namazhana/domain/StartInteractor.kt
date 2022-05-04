package kz.farabicorporation.namazhana.domain

import android.content.SharedPreferences

class StartInteractor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val PREF_FIRST_START = "first_start"
    }

    fun getIsFirstStart() = sharedPreferences.getBoolean(PREF_FIRST_START, true)

    fun startUpdate() = sharedPreferences.edit().putBoolean(PREF_FIRST_START, false).apply()

}