package com.mattrobertson.greek.reader.webview

import android.content.Context
import android.util.AttributeSet
import android.view.View

class ObservableNestedWebView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : NestedWebView(context, attrs, defStyleAttr) {

	private val observers = mutableSetOf<ScrollObserver>()

	override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
		observers.forEach {
			it.onScroll()
		}
		return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
	}

	override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
		observers.forEach {
			it.onScroll()
		}
		return super.dispatchNestedFling(velocityX, velocityY, consumed)
	}

	fun addScrollObserver(observer: ScrollObserver) = observers.add(observer)

	fun removeScrollObserver(observer: ScrollObserver) = observers.remove(observer)

}