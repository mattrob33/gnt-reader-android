package com.mattrobertson.greek.reader.presentation.bookpicker

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattrobertson.greek.reader.CoreNavigationDirections
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.ReaderActivity
import com.mattrobertson.greek.reader.data.Settings
import com.mattrobertson.greek.reader.model.GntVerseRef
import com.mattrobertson.greek.reader.presentation.HomeFragmentDirections
import com.mattrobertson.greek.reader.util.getBookTitle
import com.mattrobertson.greek.reader.util.isSingleChapterBook
import kotlinx.android.synthetic.main.book_picker_fragment.*

class BookPickerFragment : Fragment() {

    private val recentsAdapter: RecentsAdapter by lazy {

        Settings.getInstance(requireContext()).loadRecents()

        RecentsAdapter(object: RecentsAdapter.OnRecentSelectedListener {
            override fun onRecentItemSelected(ref: GntVerseRef) {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        CoreNavigationDirections.toReader(ref.book, ref.chapter)
                )
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.book_picker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recents_list.adapter = recentsAdapter
        recents_list.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val arrBooks = resources.getStringArray(R.array.books)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrBooks)
        lvBooks.adapter = adapter

        lvBooks.onItemClickListener = OnItemClickListener { _, _, book, _ ->
            if (isSingleChapterBook(book)) {
                Intent(activity, ReaderActivity::class.java).apply {
                    putExtra("book", book)
                    putExtra("chapter", 1)
                    startActivity(this)
                }
            }
            else {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        HomeFragmentDirections.toChapterPicker(book)
                )
            }
        }

        lvBooks.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val isTop = lvBooks.firstVisiblePosition == 0
                notifyScrollChange(isTop)
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
        })


    }

    private fun notifyScrollChange(isTop: Boolean) {
//        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
//                if (isTop)
//                    ColorDrawable(Color.DKGRAY)
//                else
//                    ColorDrawable(Color.LTGRAY)
//        )
    }
}