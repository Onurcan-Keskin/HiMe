package com.huawei.hime.ui.presenters

import android.widget.TextView
import translate.HmsGmsITranslator

class ViewHolderPresenter constructor(
    private val viewHolderContract: ViewHolderContract,
    private val hmsGmsTranslator: HmsGmsITranslator
) {
    fun onDetect(string: String, tvLCode: TextView, tv: TextView) {
        viewHolderContract.detect()
        hmsGmsTranslator.detectLanguage(string, tvLCode, tv)
    }
}

interface ViewHolderContract {
    fun detect()
}