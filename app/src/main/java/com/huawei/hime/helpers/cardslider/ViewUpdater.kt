package com.huawei.hime.cardslider

import android.view.View
import androidx.annotation.Nullable

abstract class ViewUpdater(protected val layoutManager: CardSliderLayoutManager) {
    open fun onLayoutManagerInitialized() {}
    abstract val activeCardPosition: Int

    @get:Nullable
    abstract val topView: View?

    abstract fun updateView()

}