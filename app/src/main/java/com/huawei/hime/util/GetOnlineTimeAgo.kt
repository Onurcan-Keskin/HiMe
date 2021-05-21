package com.huawei.hime.getTime

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.huawei.hime.R

@SuppressLint("Registered")
class GetOnlineTimeAgo : Application() {

    companion object {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
    }

    fun getTimeAgo(time: Long, ctx: Context?): String? {
        if (time < 1000000000) {
            return null
        }
        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val diff = now - time
        val timeAgo: Long
        return when {
            diff < MINUTE_MILLIS -> {
                ctx!!.getString(R.string.just_now)
            }
            diff < 2 * MINUTE_MILLIS -> {
                ctx!!.getString(R.string.minute_ago)
            }
            diff < 50 * MINUTE_MILLIS -> {
                timeAgo = diff / MINUTE_MILLIS
                "$timeAgo " + ctx!!.getString(R.string.minute_ago)
            }
            diff < 90 * MINUTE_MILLIS -> {
                ctx!!.getString(R.string.an_hour_ago)
            }
            diff < 24 * HOUR_MILLIS -> {
                timeAgo = diff / HOUR_MILLIS
                "$timeAgo " + ctx!!.getString(R.string.minute_ago)
            }
            diff < 48 * HOUR_MILLIS -> {
                ctx!!.getString(R.string.yesterday)
            }
            else -> {
                timeAgo = diff / DAY_MILLIS
                "$timeAgo " + ctx!!.getString(R.string.minute_ago)
            }
        }
    }

}