package com.mattrobertson.greek.reader.presentation.bookpicker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.ReaderActivity
import com.mattrobertson.greek.reader.presentation.home.HomeFragmentDirections
import kotlinx.android.synthetic.main.book_picker_fragment.*

class BookPickerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.book_picker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrBooks: Array<String> = resources.getStringArray(R.array.books)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrBooks)

        lvBooks.adapter = adapter
        lvBooks.onItemClickListener = OnItemClickListener { _, _, book, _ ->
            val isSingleChapterBook = (book == 17 || book == 23 || book == 24 || book == 25)
            if (isSingleChapterBook) {
                Intent(activity, ReaderActivity::class.java).apply {
                    putExtra("book", book)
                    putExtra("chapter", 1)
                    startActivity(this)
                }
            }
            else {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        HomeFragmentDirections.homeToChapterPicker(book)
                )
            }
        }
    }
}