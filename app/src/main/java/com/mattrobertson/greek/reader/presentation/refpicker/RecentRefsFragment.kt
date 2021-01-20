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
import com.mattrobertson.greek.reader.presentation.reader.ReaderViewModel
import com.mattrobertson.greek.reader.util.getReference
import kotlinx.android.synthetic.main.recent_refs_fragment.*

class RecentRefsFragment : Fragment() {

	private val readerViewModel by activityViewModels<ReaderViewModel>()

	private lateinit var navController: NavController

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.recent_refs_fragment, container, false)
		setHasOptionsMenu(true)
		return root
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		navController = requireActivity().findNavController(R.id.core_nav_host_fragment)

		(requireActivity() as AppCompatActivity).setSupportActionBar(recent_refs_toolbar)
		(requireActivity() as AppCompatActivity).supportActionBar?.apply {
			setDisplayShowTitleEnabled(false)
			setDisplayHomeAsUpEnabled(true)
		}

		val refs = Recents.getAll()

		val refTitles = refs.map { getReference(it) }

		lvRecents.apply {
			adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, refTitles)

			onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
				val ref = refs[position]
				readerViewModel.goTo(ref)
				navController.popBackStack(R.id.navigation_home, false)
			}
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> navController.navigateUp()
		}
		return true
	}
}