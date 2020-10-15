package com.mattrobertson.greek.reader.presentation.bookpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.data.Recents
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.util.getBookAbbrv

class RecentsAdapter constructor(
        private val listener: OnRecentSelectedListener
): RecyclerView.Adapter<RecentsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ref = Recents.get(position)
        holder.bookView.text = getBookAbbrv(ref.book)
        holder.chapterView.text = ref.chapter.toString()
        holder.item.setOnClickListener {
            listener.onRecentItemSelected(ref)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recents_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return Recents.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var item = view.findViewById(R.id.recents_list_item) as ConstraintLayout
        var bookView = view.findViewById(R.id.recents_list_item_book) as TextView
        var chapterView = view.findViewById(R.id.recents_list_item_chapter) as TextView
    }

    interface OnRecentSelectedListener {
        fun onRecentItemSelected(ref: VerseRef)
    }

}