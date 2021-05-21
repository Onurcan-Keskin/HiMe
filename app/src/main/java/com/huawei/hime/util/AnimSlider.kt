package com.huawei.hime.util

import android.view.View
import android.view.animation.TranslateAnimation


fun slideUpVisible(view: View) {
    view.visibility = View.VISIBLE
    val animate = TranslateAnimation(
        0F,  // fromXDelta
        0F,  // toXDelta
        view.height.toFloat(),  // fromYDelta
        0F
    ) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    view.startAnimation(animate)
}

fun slideUpGone(view: View) {
    view.visibility = View.GONE
    val animate = TranslateAnimation(
        0F,  // fromXDelta
        0F,  // toXDelta
        view.height.toFloat(),  // fromYDelta
        0F
    ) // toYDelta
    animate.duration = 500
    //animate.fillAfter = true
    view.startAnimation(animate)
}

fun slideDownVisible(view: View) {
    view.visibility = View.VISIBLE
    val animate = TranslateAnimation(
        0F,  // fromXDelta
        0F,  // toXDelta
        0F,  // fromYDelta
        view.height.toFloat()
    ) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    view.startAnimation(animate)
}

// slide the view from its current position to below itself
fun slideDownGone(view: View) {
    view.visibility = View.GONE
    val animate = TranslateAnimation(
        0F,  // fromXDelta
        0F,  // toXDelta
        0F,  // fromYDelta
        view.height.toFloat()
    ) // toYDelta
    animate.duration = 500
    //animate.fillAfter = true
    view.startAnimation(animate)
}