package com.huawei.hime.util.views

import android.view.View

fun View.setVisible(b: Boolean) {
    this.visibility = View.VISIBLE
}

fun View.setGone(){
    this.visibility = View.GONE
}

fun View.expandView(){
    val isViewVisible = when(this.visibility){
        View.VISIBLE -> true
        else -> false
    }
     if(isViewVisible){
         setGone()
     }else {
         setVisible(false)
     }
}
