package com.mattrobertson.greek.reader.presentation.refpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.data.Recents
import com.mattrobertson.greek.reader.databinding.RecentRefsFragmentBinding
import com.mattrobertson.greek.reader.presentation.reader.ReaderViewModel
import com.mattrobertson.greek.reader.util.getReference

class RecentRefsFragment : Fragment() {

	private var _binding: RecentRefsFragmentBinding? = null
	private val binding get() = _binding!!

	private val readerViewModel by activityViewModels<ReaderViewModel>()

	private lateinit var navController: NavController

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = RecentRefsFragmentBinding.inflate(inflater, container, false)

		setHasOptionsMenu(true)

		navController = requireActivity().findNavController(R.id.core_nav_host_fragment)

		(requireActivity() as AppCompatActivity).setSupportActionBar(binding.recentRefsToolbar)
		(requireActivity() as AppCompatActivity).supportActionBar?.apply {
			setDisplayShowTitleEnabled(false)
			setDisplayHomeAsUpEnabled(true)
		}

		val refs = Recents.getAll()

		val refTitles = refs.map { getReference(it) }

		binding.lvRecents.apply {
			adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, refTitles)

			onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
				val ref = refs[position]
				readerViewModel.goTo(ref)
				navController.popBackStack(R.id.navigation_home, false)
			}
		}

		return binding.root
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> navController.navigateUp()
		}
		return true
	}
}