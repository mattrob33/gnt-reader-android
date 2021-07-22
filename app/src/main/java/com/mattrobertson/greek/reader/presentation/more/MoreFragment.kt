package com.mattrobertson.greek.reader.presentation.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.databinding.MoreFragmentBinding
import com.mattrobertson.greek.reader.presentation.BottomNavHostFragmentDirections

class MoreFragment : Fragment() {

	private var _binding: MoreFragmentBinding? = null
	private val binding get() = _binding!!

	private lateinit var viewModel: MoreViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = MoreFragmentBinding.inflate(inflater, container, false)

		viewModel = ViewModelProvider(this).get(MoreViewModel::class.java)

		binding.tvSettings.setOnClickListener {
			requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
				BottomNavHostFragmentDirections.toSettings()
			)
		}

		binding.tvAbout.setOnClickListener {
			requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
				BottomNavHostFragmentDirections.toAbout()
			)
		}

		return binding.root
	}

}