package com.huawei.hime.util.views

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.huawei.hime.R

fun dayNight(context: Context) {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
        context.setTheme(R.style.DarkMode)
    } else {
        context.setTheme(R.style.LightMode)
    }
}