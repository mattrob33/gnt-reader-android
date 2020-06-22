package com.mattrobertson.greek.reader.presentation.bookpicker

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.ReaderActivity
import com.mattrobertson.greek.reader.util.dpToPx
import com.mattrobertson.greek.reader.util.numChaptersInBook
import kotlinx.android.synthetic.main.chapter_picker.*

class ChapterPickerFragment : Fragment() {

    private val args: ChapterPickerFragmentArgs by navArgs()

    var book = 0
    var numChapters = 1


    companion object {
        fun newInstance() = ChapterPickerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chapter_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        book = args.book

        numChapters = numChaptersInBook(book)

        val display: Display = requireActivity().windowManager.defaultDisplay

        val size = Point()
        display.getSize(size)
        val wScreen = size.x - 40

        var wTarget = dpToPx(requireContext(), 60)
        var numSq = wScreen / wTarget // # of squares per row


        // *** Attempted hack to fix display issues *** \\
        if (numSq < 4) {
            numSq = 4
            wTarget = wScreen / numSq
            /*
			* Note: Ideally numSq is calculated on screen size (allows for
			* devices of phones/tablets in portrait/landscape. But if a
			* is so small that it has only 1-3 per row with the preferred
			* button width, force it to have 4 per row and recalculate the
			* preferred button width based on this number.
			*/
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
                val chap = ch
                btn = ChapterButton(requireContext(), wActual)
                btn.text = "" + ch
                curRow.addView(btn)
                btn.setOnClickListener(View.OnClickListener {
                    Intent(requireContext(), ReaderActivity::class.java).apply {
                        putExtra("book", book)
                        putExtra("chapter", chap)
                        startActivity(this)
                    }
                })
                ch++
                if (ch > numChapters) break
            }
        }
    }

    internal inner class ChapterButton(c: Context?, sidePx: Int) : AppCompatButton(c) {
        init {
            layoutParams = LinearLayout.LayoutParams(sidePx, sidePx)
            gravity = Gravity.CENTER
            background = resources.getDrawable(R.drawable.chapter_button)
            setTextColor(Color.WHITE)
        }
    }

    internal inner class ButtonRow(c: Context?) : LinearLayout(c) {
        init {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = HORIZONTAL
        }
    }
}