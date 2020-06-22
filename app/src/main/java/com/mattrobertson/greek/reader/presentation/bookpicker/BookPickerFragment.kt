package com.mattrobertson.greek.reader.presentation.bookpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mattrobertson.greek.reader.R

class BookPickerFragment : Fragment() {

    companion object {
        fun newInstance() = BookPickerFragment()
    }

    private lateinit var viewModel: BookPickerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.book_picker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookPickerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}