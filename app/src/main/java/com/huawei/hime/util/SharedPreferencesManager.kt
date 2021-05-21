package com.huawei.hime.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private var mSharedPreferences: SharedPreferences
    private var mSharedLanguagePrefs: SharedPreferences

    init {
        mSharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE)
        mSharedLanguagePrefs = context.getSharedPreferences("languageName", Context.MODE_PRIVATE)
    }

    fun setNightModeState(state: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean("NightMode", state)
        editor.commit()
    }

    fun loadNightModeState(): Boolean {
        return mSharedPreferences.getBoolean("NightMode", false)
    }

    fun setLanguage(state: String) {
        val editor = mSharedLanguagePrefs.edit()
        editor.putString("tr", state)
        editor.commit()
    }

    fun loadLanguage(): String? {
        return mSharedLanguagePrefs.getString("tr","en")
    }

}