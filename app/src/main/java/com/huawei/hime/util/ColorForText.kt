package com.huawei.hime.util

import android.widget.TextView
import com.huawei.hime.R
import com.huawei.hime.util.views.setTextColorRes

fun setTextColorPrefs(textView: TextView, sharedPreferencesManager: SharedPreferencesManager){
    if (sharedPreferencesManager.loadNightModeState())
        textView.setTextColorRes(R.color.colorLeftBubble)
    else
        textView.setTextColorRes(R.color.grey)
}

fun setTintColorPrefs(textView: TextView,sharedPreferencesManager: SharedPreferencesManager){
    if (sharedPreferencesManager.loadNightModeState())
        textView.setTextColorRes(R.color.colorPureWhite)
    else
        textView.setTextColorRes(R.color.colorBlack)
}

fun setTabColorPrefs(textView: TextView, sharedPreferencesManager: SharedPreferencesManager){
    if (sharedPreferencesManager.loadNightModeState())
        textView.setTextColorRes(R.color.colorAlmondDark)
    else
        textView.setTextColorRes(R.color.colorPurple)
}