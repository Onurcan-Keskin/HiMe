package com.huawei.hime.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun showLogDebug(tag:String,string: String) {
    Log.d(tag,string)
}

fun showLogInfo(tag:String,string: String) {
    Log.i(tag,string)
}

fun showLogError(tag: String,string: String){
    Log.e(tag,string)
}

fun showSnackbar(view:View,string: String) {
    Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()
}

fun showToast(context:Context, string : String){
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}