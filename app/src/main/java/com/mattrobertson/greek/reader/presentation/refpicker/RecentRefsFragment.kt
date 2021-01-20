package com.mattrobertson.greek.reader.presentation.refpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.data.Recents
import com.mattrobertson.greek.reader.presentation.reader.ReaderViewModel
import com.mattrobertson.greek.reader.util.getBookTitle
import kotlinx.android.synthetic.main.book_picker_fragment.*

class RecentRefsFragment : Fragment() {

	private val readerViewModel by activityViewModels<ReaderViewModel>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.book_picker_fragment, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		val refs = Recents.getAll()

		val refTitles: List<String> = refs.map {
			"${getBookTitle(it.book)} ${it.chapter}"
		}

		lvBooks.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, refTitles)

		lvBooks.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
			val ref = refs[position]
			readerViewModel.goTo(ref)
			requireActivity().findNavController(R.id.core_nav_host_fragment).popBackStack(R.id.navigation_home, false)
		}
	}
}