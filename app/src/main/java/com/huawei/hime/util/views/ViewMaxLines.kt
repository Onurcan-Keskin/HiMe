package com.huawei.hime.util.views

import android.widget.TextView

fun TextView.minLine(b: Boolean){
    this.maxLines = 3
}

fun TextView.maxLine(){
    this.maxLines = 20
}

fun TextView.expandExplanation(){
    val isMaxThree = when(this.maxLines){
        3 -> true
        else -> false
    }
    if (isMaxThree){
        maxLine()
    } else {
        minLine(false)
    }
}