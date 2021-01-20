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
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.util.isSingleChapterBook
import kotlinx.android.synthetic.main.book_picker_fragment.*

class BookPickerFragment : Fragment() {

	private val refPickerViewModel by activityViewModels<RefPickerViewModel>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.book_picker_fragment, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val arrBooks = resources.getStringArray(R.array.books)
		val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrBooks)
		lvBooks.adapter = adapter

		lvBooks.onItemClickListener = AdapterView.OnItemClickListener { _, _, bookNum, _ ->
			val book = Book(bookNum)

			if (isSingleChapterBook(book)) {
				refPickerViewModel.book.value = book
				refPickerViewModel.chapter.value = 1
			} else {
				refPickerViewModel.book.value = book
			}
		}

		lvBooks.requestFocus()
	}
}