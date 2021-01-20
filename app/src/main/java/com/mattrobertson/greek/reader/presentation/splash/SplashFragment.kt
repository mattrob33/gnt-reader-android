package com.mattrobertson.greek.reader.presentation.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.presentation.BottomNavHostFragmentDirections

class SplashFragment : Fragment() {

	companion object {
		private const val SPLASH_DURATION = 1000L
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.splash_fragment, container, false)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Handler(Looper.getMainLooper()).postDelayed({
			requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
				SplashFragmentDirections.toHome()
			)
		}, SPLASH_DURATION)
	}
}