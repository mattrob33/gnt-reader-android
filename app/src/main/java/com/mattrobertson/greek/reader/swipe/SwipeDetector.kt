package com.mattrobertson.greek.reader.swipe

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import kotlin.math.abs

class SwipeDetector(
	private val swipeable: Swipeable
) : SimpleOnGestureListener() {
	
	companion object {
		private const val SWIPE_MIN_DISTANCE = 120
		private const val SWIPE_MAX_OFF_PATH = 250
		private const val SWIPE_THRESHOLD_VELOCITY = 200
	}
	
	override fun onFling(
		e1: MotionEvent,
		e2: MotionEvent,
		velocityX: Float,
		velocityY: Float
	): Boolean {
		// Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH, then dismiss the swipe.
		if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
			return false
		}

		// Check movement along the X-axis. If it exceeds SWIPE_MAX_OFF_PATH, then dismiss the swipe.
		if (abs(e1.x - e2.x) > SWIPE_MAX_OFF_PATH) {

			// Swipe from right to left.
			// The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
			// and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
			if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				swipeable.onSwipeLeft()
				return true
			}

			// Swipe from left to right.
			// The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
			// and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
			if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				swipeable.onSwipeRight()
				return true
			}
			return false
		}
		return false
	}
}