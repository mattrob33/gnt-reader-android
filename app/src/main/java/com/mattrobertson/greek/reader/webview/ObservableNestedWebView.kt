package com.mattrobertson.greek.reader.webview

import android.content.Context
import android.util.AttributeSet

class ObservableNestedWebView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : NestedWebView(context, attrs, defStyleAttr) {

	var observer: ScrollObserver? = null

	override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
		observer?.onScroll()
		return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
	}

	override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
		observer?.onScroll()
		return super.dispatchNestedFling(velocityX, velocityY, consumed)
	}
}