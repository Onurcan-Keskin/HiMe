package com.huawei.hime.cardslider

import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import java.security.InvalidParameterException
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor


class CardSnapHelper : LinearSnapHelper() {
    private var recyclerView: RecyclerView? = null

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(@Nullable recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        if (recyclerView != null
            && recyclerView.layoutManager !is CardSliderLayoutManager
        ) {
            throw InvalidParameterException(
                "LayoutManager must be instance of CardSliderLayoutManager"
            )
        }
        this.recyclerView = recyclerView
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager, velocityX: Int,
        velocityY: Int
    ): Int {
        val lm = layoutManager as CardSliderLayoutManager
        val itemCount = lm.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        val vectorForEnd =
            (lm as ScrollVectorProvider).computeScrollVectorForPosition(itemCount - 1)
                ?: return RecyclerView.NO_POSITION
        val distance = calculateScrollDistance(velocityX, velocityY)[0]
        var deltaJump: Int
        deltaJump = if (distance > 0) {
            floor(distance.toFloat() / lm.cardWidth.toDouble()).toInt()
        } else {
            ceil(distance.toFloat() / lm.cardWidth.toDouble()).toInt()
        }
        val deltaSign = Integer.signum(deltaJump)
        deltaJump = deltaSign * 3.coerceAtMost(abs(deltaJump))
        if (vectorForEnd.x < 0) {
            deltaJump = -deltaJump
        }
        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION
        }
        val currentPosition = lm.activeCardPosition
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        var targetPos = currentPosition + deltaJump
        if (targetPos < 0 || targetPos >= itemCount) {
            targetPos = RecyclerView.NO_POSITION
        }
        return targetPos
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return (layoutManager as CardSliderLayoutManager).topView
    }

    fun mCalculateDistanceToFinalSnap(
        @NonNull layoutManager: RecyclerView.LayoutManager,
        @NonNull targetView: View?
    ): IntArray {
        val lm = layoutManager as CardSliderLayoutManager
        val viewLeft = lm.getDecoratedLeft(targetView!!)
        val activeCardLeft = lm.activeCardLeft
        val activeCardCenter = lm.activeCardLeft + lm.cardWidth / 2
        val activeCardRight = lm.activeCardLeft + lm.cardWidth
        val out = intArrayOf(0, 0)
        if (viewLeft < activeCardCenter) {
            val targetPos = lm.getPosition(targetView)
            val activeCardPos = lm.activeCardPosition
            if (targetPos != activeCardPos) {
                out[0] = -(activeCardPos - targetPos) * lm.cardWidth
            } else {
                out[0] = viewLeft - activeCardLeft
            }
        } else {
            out[0] = viewLeft - activeCardRight + 1
        }
        if (out[0] != 0) {
            recyclerView!!.smoothScrollBy(out[0], 0, AccelerateInterpolator())
        }
        return intArrayOf(0, 0)
    }

    @Nullable
    override fun createScroller(layoutManager: RecyclerView.LayoutManager): SmoothScroller? {
        return (layoutManager as CardSliderLayoutManager).getSmoothScroller(recyclerView!!)
    }
}