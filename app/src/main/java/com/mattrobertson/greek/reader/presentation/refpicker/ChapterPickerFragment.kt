package com.mattrobertson.greek.reader.presentation.refpicker

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.util.dpToPx
import com.mattrobertson.greek.reader.util.getThemedColor
import com.mattrobertson.greek.reader.util.numChaptersInBook
import kotlinx.android.synthetic.main.chapter_picker.*

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
		var wTarget = dpToPx(100)
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
			textSize = dpToPx(10f)
		}
	}

	internal inner class ButtonRow(c: Context?) : LinearLayout(c) {
		init {
			layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			orientation = HORIZONTAL
		}
	}
}