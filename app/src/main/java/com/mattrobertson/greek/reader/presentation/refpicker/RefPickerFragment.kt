package com.mattrobertson.greek.reader.presentation.refpicker

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.presentation.reader.ReaderViewModel
import com.mattrobertson.greek.reader.util.dpToPx
import com.mattrobertson.greek.reader.util.getThemedColor
import com.mattrobertson.greek.reader.util.isSingleChapterBook
import com.mattrobertson.greek.reader.util.numChaptersInBook
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.book_picker_fragment.*
import kotlinx.android.synthetic.main.chapter_picker.*
import kotlinx.android.synthetic.main.ref_picker_fragment.*

@AndroidEntryPoint
class RefPickerFragment : Fragment() {

    private val readerViewModel by activityViewModels<ReaderViewModel>()
    private val refPickerViewModel by activityViewModels<RefPickerViewModel>()

    private lateinit var refPickerAdapter: RefPickerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root =  inflater.inflate(R.layout.ref_picker_fragment, container, false)
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refPickerAdapter = RefPickerAdapter(this)
        ref_picker_view_pager.adapter = refPickerAdapter

        TabLayoutMediator(ref_picker_tab_layout, ref_picker_view_pager) { tab, position ->
            tab.text = when (position) {
                0 -> "Book"
                1 -> "Chapter"
                else -> ""
            }
        }.attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(ref_picker_toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }

        refPickerViewModel.chapter.value = 0
        refPickerViewModel.book.value = null

        refPickerViewModel.book.observe(viewLifecycleOwner) { book ->
            book?.let {
                ref_picker_view_pager.post {
                    refPickerAdapter.notifyDataSetChanged()
                    ref_picker_view_pager.currentItem = 1
                }
            }
        }

        refPickerViewModel.chapter.observe(viewLifecycleOwner) { chapter ->
            if (chapter > 0) {
                val book = refPickerViewModel.book.value!!
                readerViewModel.goTo(VerseRef(book, chapter))
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
        }
        return true
    }

    inner class RefPickerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BookPickerFragment()
                1 -> ChapterPickerFragment()
                else -> throw IllegalStateException("RefPickerAdapter only has 2 items but position was $position")
            }
        }
    }

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

    class ChapterPickerFragment : Fragment() {

        private val refPickerViewModel by activityViewModels<RefPickerViewModel>()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.chapter_picker, container, false)
        }

        override fun onPause() {
            cp_container.removeAllViews()
            super.onPause()
        }

        override fun onResume() {
            super.onResume()

            cp_container.removeAllViews()

            if (refPickerViewModel.book.value == null)
                refPickerViewModel.book.value = Book.MATTHEW

            val book = refPickerViewModel.book.value ?: Book.MATTHEW

            val numChapters = numChaptersInBook(book)

            val display = requireActivity().windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val wScreen = size.x - 40
            var wTarget = dpToPx(requireContext(), 100)
            var numSq = wScreen / wTarget // # of squares per row

            if (numSq < 4) {
                numSq = 4
                wTarget = wScreen / numSq
            }

            val rem = wScreen % wTarget // total remaining space on each row
            val remPer = rem / numSq // remaining pixels per square, per row
            val wActual = wTarget + remPer
            val numRows = (numChapters + numSq - 1) / numSq // # of rows needed

            var curRow: ButtonRow
            var btn: ChapterButton

            var ch = 1

            for (i in 0 until numRows) {
                curRow = ButtonRow(requireContext())
                cp_container.addView(curRow)
                for (j in 0 until numSq) {
                    val chapter = ch
                    btn = ChapterButton(requireContext(), wActual)
                    btn.text = "$ch"
                    curRow.addView(btn)
                    btn.setOnClickListener {
                        refPickerViewModel.chapter.value = chapter
                    }
                    ch++
                    if (ch > numChapters) break
                }
            }
        }

        internal inner class ChapterButton(c: Context, sidePx: Int) : AppCompatButton(c) {
            init {
                layoutParams = LinearLayout.LayoutParams(sidePx, sidePx)
                gravity = Gravity.CENTER
                background = ColorDrawable(Color.TRANSPARENT)
                setTextColor(getThemedColor(c, R.attr.colorOnSurface))
                textSize = dpToPx(c, 10f)
            }
        }

        internal inner class ButtonRow(c: Context?) : LinearLayout(c) {
            init {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                orientation = HORIZONTAL
            }
        }
    }
}