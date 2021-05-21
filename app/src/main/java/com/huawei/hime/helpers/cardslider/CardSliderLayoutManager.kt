package com.huawei.hime.cardslider

import android.content.Context
import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import com.huawei.hime.R
import java.lang.reflect.InvocationTargetException
import java.util.*


class CardSliderLayoutManager : RecyclerView.LayoutManager, ScrollVectorProvider {
    private val viewCache = SparseArray<View?>()
    private val cardsXCoords = SparseIntArray()
    var cardWidth = 0
    var activeCardLeft = 0
    var activeCardRight = 0
    var activeCardCenter = 0
        private set
    var cardsGap = 0f
        private set
    private var scrollRequestedPosition = 0
    private var viewUpdater: ViewUpdater? = null
    /**
     * Constructor used when layout manager is set in XML by RecyclerView attribute
     * "layoutManager".
     *
     * @attr ref R.styleable#CardSlider_activeCardLeftOffset
     * @attr ref R.styleable#CardSlider_cardWidth
     * @attr ref R.styleable#CardSlider_cardsGap
     */
    /**
     * Creates CardSliderLayoutManager with default values
     *
     * @param context Current context, will be used to access resources.
     */
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) {
        val density = context.resources.displayMetrics.density
        val defaultCardWidth =
            (DEFAULT_CARD_WIDTH * density).toInt()
        val defaultActiveCardLeft =
            (DEFAULT_ACTIVE_CARD_LEFT_OFFSET * density).toInt()
        val defaultCardsGap =
            DEFAULT_CARDS_GAP * density
        if (attrs == null) {
            initialize(defaultActiveCardLeft, defaultCardWidth, defaultCardsGap, null)
        } else {
            val attrCardWidth: Int
            val attrActiveCardLeft: Int
            val attrCardsGap: Float
            val viewUpdateClassName: String
            val a =
                context.theme.obtainStyledAttributes(attrs, R.styleable.CardSlider, 0, 0)
            try {
                attrCardWidth =
                    a.getDimensionPixelSize(R.styleable.CardSlider_cardWidth, defaultCardWidth)
                attrActiveCardLeft = a.getDimensionPixelSize(
                    R.styleable.CardSlider_activeCardLeftOffset,
                    defaultActiveCardLeft
                )
                attrCardsGap = a.getDimension(R.styleable.CardSlider_cardsGap, defaultCardsGap)
                viewUpdateClassName = a.getString(R.styleable.CardSlider_viewUpdater).toString()
            } finally {
                a.recycle()
            }
            val viewUpdater = loadViewUpdater(context, viewUpdateClassName, attrs)
            initialize(attrActiveCardLeft, attrCardWidth, attrCardsGap, viewUpdater)
        }
    }

    constructor(activeCardLeft: Int, cardWidth: Int, cardsGap: Float) {
        initialize(activeCardLeft, cardWidth, cardsGap, null)
    }

    private fun initialize(
        left: Int,
        width: Int,
        gap: Float,
        updater: ViewUpdater?
    ) {
        cardWidth = width
        activeCardLeft = left
        activeCardRight = activeCardLeft + cardWidth
        activeCardCenter = activeCardLeft + (activeCardRight - activeCardLeft) / 2
        cardsGap = gap
        viewUpdater = updater
        if (viewUpdater == null) {
            viewUpdater = DefaultViewUpdater(this)
        }
        viewUpdater!!.onLayoutManagerInitialized()
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(
        recycler: Recycler,
        state: RecyclerView.State
    ) {
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        if (childCount == 0 && state.isPreLayout) {
            return
        }
        var anchorPos = activeCardPosition
        if (state.isPreLayout) {
            val removedPositions: LinkedList<Int> = LinkedList()
            var i = 0
            val cnt = childCount
            while (i < cnt) {
                val child = getChildAt(i)
                if (child != null) {
                    val isRemoved =
                        (child.layoutParams as RecyclerView.LayoutParams).isItemRemoved
                    if (isRemoved) {
                        removedPositions.add(getPosition(child))
                    }
                }
                i++
            }
            if (removedPositions.contains(anchorPos)) {
                val last = removedPositions.last
                val first = removedPositions.first
                val right = Math.min(last, itemCount - 1)
                var left = right
                if (last != first) {
                    left = Math.max(first, 0)
                }
                anchorPos = Math.max(left, right)
            }
        }
        detachAndScrapAttachedViews(recycler)
        fill(anchorPos, recycler, state)
        if (cardsXCoords.size() != 0) {
            layoutByCoords()
        }
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        removeAllViews()
    }

    override fun canScrollHorizontally(): Boolean {
        return childCount != 0
    }

    override fun scrollToPosition(position: Int) {
        if (position < 0 || position >= itemCount) {
            return
        }
        scrollRequestedPosition = position
        requestLayout()
    }

    override fun scrollHorizontallyBy(
        dx: Int, recycler: Recycler,
        state: RecyclerView.State
    ): Int {
        scrollRequestedPosition = RecyclerView.NO_POSITION
        val delta: Int
        delta = if (dx < 0) {
            scrollRight(Math.max(dx, -cardWidth))
        } else {
            scrollLeft(dx)
        }
        fill(activeCardPosition, recycler, state)
        cardsXCoords.clear()
        var i = 0
        val cnt = childCount
        while (i < cnt) {
            val view = getChildAt(i)
            cardsXCoords.put(getPosition(view!!), getDecoratedLeft(view))
            i++
        }
        return delta
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        return PointF((targetPosition - activeCardPosition).toFloat(), 0F)
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State,
        position: Int
    ) {
        if (position < 0 || position >= itemCount) {
            return
        }
        val scroller = getSmoothScroller(recyclerView)
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }

    override fun onItemsRemoved(
        recyclerView: RecyclerView,
        positionStart: Int,
        count: Int
    ) {
        val anchorPos = activeCardPosition
        if (positionStart + count <= anchorPos) {
            scrollRequestedPosition = anchorPos - 1
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = SavedState()
        state.anchorPos = activeCardPosition
        return state
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        if (parcelable is SavedState) {
            scrollRequestedPosition = parcelable.anchorPos
            requestLayout()
        }
    }

    /**
     * @return active card position or RecyclerView.NO_POSITION
     */
    val activeCardPosition: Int
        get() = if (scrollRequestedPosition != RecyclerView.NO_POSITION) {
            scrollRequestedPosition
        } else {
            viewUpdater!!.activeCardPosition
        }

    val topView: View?
        get() = viewUpdater!!.topView

    fun getSmoothScroller(recyclerView: RecyclerView): LinearSmoothScroller {
        return object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateTimeForDeceleration(dx: Int): Int {
                return (super.calculateTimeForDeceleration(dx) * 0.5).toInt()
            }

            override fun calculateDxToMakeVisible(
                view: View,
                snapPreference: Int
            ): Int {
                val viewStart = getDecoratedLeft(view)
                return if (viewStart > activeCardLeft) {
                    activeCardLeft - viewStart
                } else {
                    var delta = 0
                    var topViewPos = 0
                    val topView = topView
                    if (topView != null) {
                        topViewPos = getPosition(topView)
                        if (topViewPos != targetPosition) {
                            val topViewLeft = getDecoratedLeft(topView)
                            if (topViewLeft >= activeCardLeft && topViewLeft < activeCardRight) {
                                delta = activeCardRight - topViewLeft
                            }
                        }
                    }
                    delta + cardWidth * Math.max(
                        0,
                        topViewPos - targetPosition - 1
                    )
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 0.5f
            }
        }
    }

    private fun loadViewUpdater(
        context: Context,
        className: String?,
        attrs: AttributeSet
    ): ViewUpdater? {
        if (className == null || className.trim { it <= ' ' }.length == 0) {
            return null
        }
        val fullClassName: String
        fullClassName = if (className[0] == '.') {
            context.packageName + className
        } else if (className.contains(".")) {
            className
        } else {
            CardSliderLayoutManager::class.java.getPackage()!!.name + '.' + className
        }
        val updater: ViewUpdater
        try {
            val classLoader = context.classLoader
            val viewUpdaterClass =
                classLoader.loadClass(fullClassName).asSubclass(
                    ViewUpdater::class.java
                )
            val constructor =
                viewUpdaterClass.getConstructor(
                    CardSliderLayoutManager::class.java
                )
            constructor.isAccessible = true
            updater = constructor.newInstance(this)
        } catch (e: NoSuchMethodException) {
            throw IllegalStateException(
                attrs.positionDescription + ": Error creating LayoutManager " + className, e
            )
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException(
                attrs.positionDescription + ": Unable to find ViewUpdater" + className, e
            )
        } catch (e: InvocationTargetException) {
            throw IllegalStateException(
                attrs.positionDescription + ": Could not instantiate the ViewUpdater: " + className,
                e
            )
        } catch (e: InstantiationException) {
            throw IllegalStateException(
                attrs.positionDescription + ": Could not instantiate the ViewUpdater: " + className,
                e
            )
        } catch (e: IllegalAccessException) {
            throw IllegalStateException(
                attrs.positionDescription + ": Cannot access non-public constructor " + className,
                e
            )
        } catch (e: ClassCastException) {
            throw IllegalStateException(
                attrs.positionDescription + ": Class is not a ViewUpdater " + className, e
            )
        }
        return updater
    }

    private fun scrollRight(dx: Int): Int {
        val childCount = childCount
        if (childCount == 0) {
            return 0
        }
        val rightestView = getChildAt(childCount - 1)
        val deltaBorder = activeCardLeft + getPosition(rightestView!!) * cardWidth
        val delta = getAllowedRightDelta(rightestView, dx, deltaBorder)
        val rightViews: LinkedList<View?> = LinkedList()
        val leftViews: LinkedList<View?> = LinkedList()
        for (i in childCount - 1 downTo 0) {
            val view = getChildAt(i)
            val viewLeft = getDecoratedLeft(view!!)
            if (viewLeft >= activeCardRight) {
                rightViews.add(view)
            } else {
                leftViews.add(view)
            }
        }
        for (view in rightViews) {
            val border = activeCardLeft + getPosition(view!!) * cardWidth
            val allowedDelta = getAllowedRightDelta(view, dx, border)
            view.offsetLeftAndRight(-allowedDelta)
        }
        val step = activeCardLeft / LEFT_CARD_COUNT
        val jDelta = Math.floor(1f * delta * step / cardWidth.toDouble()).toInt()
        var prevView: View? = null
        var j = 0
        var i = 0
        val cnt: Int = leftViews.size
        while (i < cnt) {
            val view = leftViews[i]!!
            if (prevView == null || getDecoratedLeft(prevView) >= activeCardRight) {
                val border = activeCardLeft + getPosition(view) * cardWidth
                val allowedDelta = getAllowedRightDelta(view, dx, border)
                view.offsetLeftAndRight(-allowedDelta)
            } else {
                val border = activeCardLeft - step * j
                view.offsetLeftAndRight(-getAllowedRightDelta(view, jDelta, border))
                j++
            }
            prevView = view
            i++
        }
        return delta
    }

    private fun scrollLeft(dx: Int): Int {
        val childCount = childCount
        if (childCount == 0) {
            return 0
        }
        val lastView = getChildAt(childCount - 1)
        val isLastItem = getPosition(lastView!!) == itemCount - 1
        val delta: Int
        delta = if (isLastItem) {
            Math.min(dx, getDecoratedRight(lastView) - activeCardRight)
        } else {
            dx
        }
        val step = activeCardLeft / LEFT_CARD_COUNT
        val jDelta = Math.ceil(1f * delta * step / cardWidth.toDouble()).toInt()
        for (i in childCount - 1 downTo 0) {
            val view = getChildAt(i)
            val viewLeft = getDecoratedLeft(view!!)
            if (viewLeft > activeCardLeft) {
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, activeCardLeft))
            } else {
                var border = activeCardLeft - step
                for (j in i downTo 0) {
                    val jView = getChildAt(j)
                    jView!!.offsetLeftAndRight(getAllowedLeftDelta(jView, jDelta, border))
                    border -= step
                }
                break
            }
        }
        return delta
    }

    private fun getAllowedLeftDelta(view: View, dx: Int, border: Int): Int {
        val viewLeft = getDecoratedLeft(view)
        return if (viewLeft - dx > border) {
            -dx
        } else {
            border - viewLeft
        }
    }

    private fun getAllowedRightDelta(view: View, dx: Int, border: Int): Int {
        val viewLeft = getDecoratedLeft(view)
        return if (viewLeft + Math.abs(dx) < border) {
            dx
        } else {
            viewLeft - border
        }
    }

    private fun layoutByCoords() {
        val count = Math.min(childCount, cardsXCoords.size())
        for (i in 0 until count) {
            val view = getChildAt(i)
            val viewLeft = cardsXCoords[getPosition(view!!)]
            layoutDecorated(view, viewLeft, 0, viewLeft + cardWidth, getDecoratedBottom(view))
        }
        updateViewScale()
        cardsXCoords.clear()
    }

    private fun fill(anchorPos: Int, recycler: Recycler, state: RecyclerView.State) {
        viewCache.clear()
        run {
            var i = 0
            val cnt = childCount
            while (i < cnt) {
                val view = getChildAt(i)
                val pos = getPosition(view!!)
                viewCache.put(pos, view)
                i++
            }
        }
        run {
            var i = 0
            val cnt = viewCache.size()
            while (i < cnt) {
                detachView(viewCache.valueAt(i)!!)
                i++
            }
        }
        if (!state.isPreLayout) {
            fillLeft(anchorPos, recycler)
            fillRight(anchorPos, recycler)
        }
        var i = 0
        val cnt = viewCache.size()
        while (i < cnt) {
            recycler.recycleView(viewCache.valueAt(i)!!)
            i++
        }
        updateViewScale()
    }

    private fun fillLeft(anchorPos: Int, recycler: Recycler) {
        if (anchorPos == RecyclerView.NO_POSITION) {
            return
        }
        val layoutStep = activeCardLeft / LEFT_CARD_COUNT
        var pos =
            Math.max(0, anchorPos - LEFT_CARD_COUNT - 1)
        var viewLeft = Math.max(
            -1,
            LEFT_CARD_COUNT - (anchorPos - pos)
        ) * layoutStep
        while (pos < anchorPos) {
            var view = viewCache[pos]
            if (view != null) {
                attachView(view)
                viewCache.remove(pos)
            } else {
                view = recycler.getViewForPosition(pos)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val viewHeight = getDecoratedMeasuredHeight(view)
                layoutDecorated(view, viewLeft, 0, viewLeft + cardWidth, viewHeight)
            }
            viewLeft += layoutStep
            pos++
        }
    }

    private fun fillRight(anchorPos: Int, recycler: Recycler) {
        if (anchorPos == RecyclerView.NO_POSITION) {
            return
        }
        val width = width
        val itemCount = itemCount
        var pos = anchorPos
        var viewLeft = activeCardLeft
        var fillRight = true
        while (fillRight && pos < itemCount) {
            var view = viewCache[pos]
            if (view != null) {
                attachView(view)
                viewCache.remove(pos)
            } else {
                view = recycler.getViewForPosition(pos)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val viewHeight = getDecoratedMeasuredHeight(view)
                layoutDecorated(view, viewLeft, 0, viewLeft + cardWidth, viewHeight)
            }
            viewLeft = getDecoratedRight(view)
            fillRight = viewLeft < width + cardWidth
            pos++
        }
    }

    private fun updateViewScale() {
        viewUpdater!!.updateView()
    }

    private class SavedState : Parcelable {
        var anchorPos = 0

        internal constructor() {}
        internal constructor(`in`: Parcel) {
            anchorPos = `in`.readInt()
        }

        constructor(other: SavedState) {
            anchorPos = other.anchorPos
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeInt(anchorPos)
        }

        companion object {
            val CREATOR: Creator<SavedState> =
                object : Creator<SavedState> {
                    override fun createFromParcel(parcel: Parcel): SavedState? {
                        return SavedState(parcel)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    companion object {
        private const val DEFAULT_ACTIVE_CARD_LEFT_OFFSET = 50
        private const val DEFAULT_CARD_WIDTH = 148
        private const val DEFAULT_CARDS_GAP = 12
        private const val LEFT_CARD_COUNT = 2
    }
}