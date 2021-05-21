package com.huawei.hime.helpers

import android.widget.TextView

interface ITranslator {
    fun detectLanguage(sourceText: String, tvLCode:TextView, tv: TextView)
    fun initOnCreate()
    fun translateText(sourceText: String, sourceLangCode: String, tv: TextView)
}