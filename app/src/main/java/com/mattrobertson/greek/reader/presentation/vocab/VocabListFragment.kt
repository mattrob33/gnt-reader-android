package com.mattrobertson.greek.reader.presentation.vocab

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mattrobertson.greek.reader.MyVocabActivity
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.util.getBookTitle
import kotlinx.android.synthetic.main.vocab_list_fragment.*

class VocabListFragment : Fragment() {

    private val viewModelFactory: VocabListViewModelFactory by lazy {
        VocabListViewModelFactory(requireContext().applicationContext)
    }

    private val viewModel: VocabListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(VocabListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.vocab_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lvWords.emptyView = emptyListItem

        val c = viewModel.getCursor()
        val adapter = VocabAdapter(requireContext(), c)
        lvWords.adapter = adapter
        lvWords.setOnItemClickListener { _, _, _, id ->
            val (book, chapter) = viewModel.getBookAndChapter(id)
            Intent(activity, MyVocabActivity::class.java).apply {
                putExtra("book", book)
                putExtra("chapter", chapter)
                startActivity(this)
            }
        }
    }

    class VocabAdapter(context: Context, c: Cursor?) : CursorAdapter(context, c) {

        override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.vocab_list_item, parent, false)
        }

        override fun bindView(view: View, context: Context, cursor: Cursor) {
            val tvItem = view.findViewById<View>(R.id.tvVocabItem) as TextView
            val book = cursor.getInt(cursor.getColumnIndexOrThrow("book"))
            val chapter = cursor.getInt(cursor.getColumnIndexOrThrow("chapter"))
            val chapterText = "${getBookTitle(book)} $chapter"
            tvItem.text = chapterText
        }
    }
}