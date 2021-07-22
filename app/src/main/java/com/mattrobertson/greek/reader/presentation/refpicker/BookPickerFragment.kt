package com.mattrobertson.greek.reader.presentation.refpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.databinding.BookPickerFragmentBinding
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.util.isSingleChapterBook

class BookPickerFragment : Fragment() {

	private var _binding: BookPickerFragmentBinding? = null
	private val binding get() = _binding!!

	private val refPickerViewModel by activityViewModels<RefPickerViewModel>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = BookPickerFragmentBinding.inflate(inflater, container, false)

		val arrBooks = resources.getStringArray(R.array.books)
		val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrBooks)
		binding.lvBooks.adapter = adapter

		binding.lvBooks.onItemClickListener = AdapterView.OnItemClickListener { _, _, bookNum, _ ->
			val book = Book(bookNum)

			if (isSingleChapterBook(book)) {
				refPickerViewModel.book.value = book
				refPickerViewModel.chapter.value = 1
			} else {
				refPickerViewModel.book.value = book
			}
		}

		binding.lvBooks.requestFocus()

		return binding.root
	}
}