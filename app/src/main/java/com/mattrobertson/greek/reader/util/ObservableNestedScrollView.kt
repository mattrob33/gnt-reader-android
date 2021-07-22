package com.mattrobertson.greek.reader.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView

class ObservableNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): NestedScrollView(context, attrs, defStyleAttr) {

    interface OnNestedPreScroll {
        fun onNestedPreScroll()
    }

    var observer: OnNestedPreScroll? = null

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(target, dx, dy, consumed, type)
        observer?.onNestedPreScroll()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(target, dx, dy, consumed)
        observer?.onNestedPreScroll()
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        observer?.onNestedPreScroll()
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        observer?.onNestedPreScroll()
        return super.onStartNestedScroll(child, target, nestedScrollAxes)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        observer?.onNestedPreScroll()
        return super.onStartNestedScroll(child, target, axes, type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        observer?.onNestedPreScroll()
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        observer?.onNestedPreScroll()
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        observer?.onNestedPreScroll()
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        observer?.onNestedPreScroll()
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        observer?.onNestedPreScroll()
        super.onScrollChanged(l, t, oldl, oldt)
    }
}