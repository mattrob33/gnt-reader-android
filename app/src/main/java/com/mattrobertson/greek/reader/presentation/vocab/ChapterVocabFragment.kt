package com.mattrobertson.greek.reader.presentation.vocab

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.CursorAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.daimajia.swipe.SwipeLayout
import com.mattrobertson.greek.reader.MyVocabActivity
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.dialog.VocabWizardDialog
import com.mattrobertson.greek.reader.interfaces.VocabWizardDialogInterface
import kotlinx.android.synthetic.main.chapter_vocab_fragment.*

class ChapterVocabFragment : Fragment(), VocabWizardDialogInterface {

    private val args: ChapterVocabFragmentArgs by navArgs()

    private val viewModelFactory: ChapterVocabViewModelFactory by lazy {
        ChapterVocabViewModelFactory(requireContext().applicationContext, args.book, args.chapter)
    }

    private val viewModel: ChapterVocabViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ChapterVocabViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chapter_vocab_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refreshAdapter()

        val wizard = VocabWizardDialog(requireActivity(), this)
        wizard.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
        }
        fabWizard.setOnClickListener {
            wizard.show()
        }

        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.cursorVersion.observe(viewLifecycleOwner) {
            refreshAdapter()
            chapter_vocab_progress_bar.visibility = View.INVISIBLE
        }
    }

    private fun refreshAdapter() {
        val adapter = VocabSwipeAdapter(requireContext(), viewModel.getCursor())
        lvMyVocab.adapter = adapter
    }

    override fun onVocabWizardGo(level: Int) {
        if (level == VocabWizardDialogInterface.DELETE_ALL) {
            viewModel.deleteAllVocabForChapter()
            refreshAdapter()
        }
        else {
            chapter_vocab_progress_bar.visibility = View.VISIBLE
            viewModel.autoBuildWordList(level)
        }
    }

    inner class VocabSwipeAdapter(context: Context, c: Cursor?) : CursorAdapter(context, c) {

        private val greekFont = Typeface.createFromAsset(context.assets, "fonts/sblgreek.ttf")

        override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.vocab_swipe_list_item, parent, false)
        }

        override fun bindView(view: View, context: Context, cursor: Cursor) {
            val swipeLayout = view.findViewById<View>(R.id.swipe_layout) as SwipeLayout
            val btnDelete = view.findViewById<View>(R.id.delete) as RelativeLayout

            val tvLex = view.findViewById<View>(R.id.tvVocabLex) as TextView
            val tvGloss = view.findViewById<View>(R.id.tvVocabGloss) as TextView

            swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
            tvLex.typeface = greekFont

            val lex = cursor.getString(cursor.getColumnIndexOrThrow("lex"))
            val gloss = cursor.getString(cursor.getColumnIndexOrThrow("gloss"))
            val occ = cursor.getInt(cursor.getColumnIndexOrThrow("occ"))

            tvLex.text = lex
            tvGloss.text = "$gloss [$occ]"

            btnDelete.setOnClickListener {
                viewModel.deleteVocabWord(lex)
                swipeLayout.close()
                refreshAdapter()
            }
        }
    }
}