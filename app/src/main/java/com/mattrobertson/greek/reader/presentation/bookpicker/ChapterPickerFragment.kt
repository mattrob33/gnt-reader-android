package com.mattrobertson.greek.reader.presentation.bookpicker

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mattrobertson.greek.reader.CoreNavigationDirections
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.ReaderActivity
import com.mattrobertson.greek.reader.util.dpToPx
import com.mattrobertson.greek.reader.util.numChaptersInBook
import kotlinx.android.synthetic.main.chapter_picker.*

class ChapterPickerFragment : Fragment() {

    private val args: ChapterPickerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chapter_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numChapters = numChaptersInBook(args.book)

        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val wScreen = size.x - 40
        var wTarget = dpToPx(requireContext(), 60)
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
                    launchReader(args.book, chapter)
                }
                ch++
                if (ch > numChapters) break
            }
        }
    }

    private fun launchReader(book: Int, chapter: Int) {
        requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                CoreNavigationDirections.toReader(book, chapter)
        )
    }

    internal inner class ChapterButton(c: Context?, sidePx: Int) : AppCompatButton(c) {
        init {
            layoutParams = LinearLayout.LayoutParams(sidePx, sidePx)
            gravity = Gravity.CENTER
            background = ResourcesCompat.getDrawable(resources, R.drawable.chapter_button, null)
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