package com.huawei.hime.util.views

fun String?.toSafeString() : String{
    return this ?: ""
}