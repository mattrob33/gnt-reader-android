package com.mattrobertson.greek.reader.presentation.refpicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.util.numChaptersInBook
import kotlinx.android.synthetic.main.chapter_picker.*


class ChapterPickerFragment : Fragment() {

	private val refPickerViewModel by activityViewModels<RefPickerViewModel>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.chapter_picker, container, false)
	}

	override fun onResume() {
		super.onResume()

		if (refPickerViewModel.book.value == null)
			refPickerViewModel.book.value = Book.MATTHEW

		val book = refPickerViewModel.book.value ?: Book.MATTHEW

		val numChapters = numChaptersInBook(book)

		val chapters = (1..numChapters).toList()
		
		val adapter = ChaptersAdapter(requireContext(), chapters.map { it.toString() }).apply {
			setClickListener(object : ChaptersAdapter.ItemClickListener {
				override fun onItemClick(position: Int) {
					refPickerViewModel.chapter.value = chapters[position]
				}
			})
		}

		rv_chapters.layoutManager = GridLayoutManager(requireContext(), 4)
		rv_chapters.adapter = adapter
	}

	internal class ChaptersAdapter(
		context: Context,
		private val data: List<String>
	) : RecyclerView.Adapter<ChaptersAdapter.ViewHolder>() {

		private var mClickListener: ItemClickListener? = null

		private val inflater: LayoutInflater = LayoutInflater.from(context)


		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
			val view: View = inflater.inflate(R.layout.chapter_picker_item, parent, false)
			return ViewHolder(view)
		}

		override fun onBindViewHolder(holder: ViewHolder, position: Int) {
			holder.textItem.text = data[position]
		}

		override fun getItemCount() = data.size

		inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

			val textItem: TextView = itemView.findViewById(R.id.chapter_item)

			override fun onClick(view: View) {
				mClickListener?.onItemClick(adapterPosition)
			}

			init {
				itemView.setOnClickListener(this)
			}
		}

		fun setClickListener(itemClickListener: ItemClickListener?) {
			mClickListener = itemClickListener
		}

		interface ItemClickListener {
			fun onItemClick(position: Int)
		}
	}
}