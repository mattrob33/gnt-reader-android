package com.mattrobertson.greek.reader.presentation.more

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.presentation.HomeFragmentDirections
import kotlinx.android.synthetic.main.more_fragment.*

class MoreFragment : Fragment() {

	companion object {
		fun newInstance() = MoreFragment()
	}

	private lateinit var viewModel: MoreViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.more_fragment, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		viewModel = ViewModelProvider(this).get(MoreViewModel::class.java)

		tvSettings.setOnClickListener {
			requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
				HomeFragmentDirections.toSettings()
			)
		}

		tvAbout.setOnClickListener {
			requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
				HomeFragmentDirections.toAbout()
			)
		}
	}

}