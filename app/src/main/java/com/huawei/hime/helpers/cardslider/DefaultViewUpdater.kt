package com.huawei.hime.cardslider

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView


class DefaultViewUpdater(layoutManager: CardSliderLayoutManager?) :
    ViewUpdater(layoutManager!!) {
    private var cardWidth = 0
    private var activeCardLeft = 0
    private var activeCardRight = 0
    private var activeCardCenter = 0
    private var cardsGap = 0f
    private var transitionEnd = 0
    private var transitionDistance = 0
    private var transitionRight2Center = 0f
    override fun onLayoutManagerInitialized() {
        cardWidth = layoutManager.cardWidth
        activeCardLeft = layoutManager.activeCardLeft
        activeCardRight = layoutManager.activeCardRight
        activeCardCenter = layoutManager.activeCardCenter
        cardsGap = layoutManager.cardsGap
        transitionEnd = activeCardCenter
        transitionDistance = activeCardRight - transitionEnd
        val centerBorder =
            (cardWidth - cardWidth * SCALE_CENTER) / 2f
        val rightBorder =
            (cardWidth - cardWidth * SCALE_RIGHT) / 2f
        val right2centerDistance =
            activeCardRight + centerBorder - (activeCardRight - rightBorder)
        transitionRight2Center = right2centerDistance - cardsGap
    }

    override val activeCardPosition: Int
        get() {
            var result = RecyclerView.NO_POSITION
            var biggestView: View? = null
            var lastScaleX = 0f
            var i = 0
            val cnt = layoutManager.childCount
            while (i < cnt) {
                val child = layoutManager.getChildAt(i)
                val viewLeft = layoutManager.getDecoratedLeft(child!!)
                if (viewLeft >= activeCardRight) {
                    i++
                    continue
                }
                val scaleX = child.scaleX
                if (lastScaleX < scaleX && viewLeft < activeCardCenter) {
                    lastScaleX = scaleX
                    biggestView = child
                }
                i++
            }
            if (biggestView != null) {
                result = layoutManager.getPosition(biggestView)
            }
            return result
        }

    override val topView: View?
        get() {
            if (layoutManager.childCount == 0) {
                return null
            }
            var result: View? = null
            var lastValue = cardWidth.toFloat()
            var i = 0
            val cnt = layoutManager.childCount
            while (i < cnt) {
                val child = layoutManager.getChildAt(i)
                if (layoutManager.getDecoratedLeft(child!!) > activeCardRight) {
                    i++
                    continue
                }
                val viewLeft = layoutManager.getDecoratedLeft(child)
                val diff = activeCardRight - viewLeft
                if (diff < lastValue) {
                    lastValue = diff.toFloat()
                    result = child
                }
                i++
            }
            return result
        }

    override fun updateView() {
        var prevView: View? = null
        var i = 0
        val cnt = layoutManager.childCount
        while (i < cnt) {
            val view = layoutManager.getChildAt(i)
            val viewLeft = layoutManager.getDecoratedLeft(view!!)
            val scale: Float
            val alpha: Float
            val z: Float
            val x: Float
            if (viewLeft < activeCardLeft) {
                val ratio = viewLeft.toFloat() / activeCardLeft
                scale =
                    SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio
                alpha = 0.1f + ratio
                z = Z_CENTER_1 * ratio
                x = 0f
            } else if (viewLeft < activeCardCenter) {
                scale = SCALE_CENTER
                alpha = 1f
                z = Z_CENTER_1.toFloat()
                x = 0f
            } else if (viewLeft < activeCardRight) {
                val ratio =
                    (viewLeft - activeCardCenter).toFloat() / (activeCardRight - activeCardCenter)
                scale =
                    SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio
                alpha = 1f
                z = Z_CENTER_2.toFloat()
                x = -transitionRight2Center.coerceAtMost(transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance)
            } else {
                scale = SCALE_RIGHT
                alpha = 1f
                z = Z_RIGHT.toFloat()
                if (prevView != null) {
                    val prevViewScale: Float
                    val prevTransition: Float
                    val prevRight: Int
                    val isFirstRight =
                        layoutManager.getDecoratedRight(prevView) <= activeCardRight
                    if (isFirstRight) {
                        prevViewScale = SCALE_CENTER
                        prevRight = activeCardRight
                        prevTransition = 0f
                    } else {
                        prevViewScale = prevView.scaleX
                        prevRight = layoutManager.getDecoratedRight(prevView)
                        prevTransition = prevView.translationX
                    }
                    val prevBorder = (cardWidth - cardWidth * prevViewScale) / 2
                    val currentBorder =
                        (cardWidth - cardWidth * SCALE_RIGHT) / 2
                    val distance =
                        viewLeft + currentBorder - (prevRight - prevBorder + prevTransition)
                    val transition = distance - cardsGap
                    x = -transition
                } else {
                    x = 0f
                }
            }
            onUpdateViewScale(view, scale)
            onUpdateViewTransitionX(view, x)
            onUpdateViewZ(view, z)
            onUpdateViewAlpha(view, alpha)
            prevView = view
            i++
        }
    }

    protected fun onUpdateViewAlpha(view: View, alpha: Float) {
        if (view.alpha != alpha) {
            view.alpha = alpha
        }
    }

    protected fun onUpdateViewScale(view: View, scale: Float) {
        if (view.scaleX != scale) {
            view.scaleX = scale
            view.scaleY = scale
        }
    }

    protected fun onUpdateViewZ(view: View, z: Float) {
        if (ViewCompat.getZ(view) != z) {
            ViewCompat.setZ(view, z)
        }
    }

    protected fun onUpdateViewTransitionX(view: View, x: Float) {
        if (view.translationX != x) {
            view.translationX = x
        }
    }

    companion object {
        private const val SCALE_LEFT = 0.65f
        private const val SCALE_CENTER = 0.95f
        private const val SCALE_RIGHT = 0.8f
        private const val SCALE_CENTER_TO_LEFT =
            SCALE_CENTER - SCALE_LEFT
        private const val SCALE_CENTER_TO_RIGHT =
            SCALE_CENTER - SCALE_RIGHT
        private const val Z_CENTER_1 = 12
        private const val Z_CENTER_2 = 16
        private const val Z_RIGHT = 8
    }
}