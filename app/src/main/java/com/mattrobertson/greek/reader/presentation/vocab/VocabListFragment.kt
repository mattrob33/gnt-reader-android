package com.mattrobertson.greek.reader.presentation.vocab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mattrobertson.greek.reader.R

class VocabListFragment : Fragment() {

    companion object {
        fun newInstance() = VocabListFragment()
    }

    private lateinit var viewModel: VocabListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.vocab_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VocabListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}