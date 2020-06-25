package com.mattrobertson.greek.reader.presentation.reader

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.util.AppConstants
import kotlinx.android.synthetic.main.reader.*

class ReaderFragment : Fragment() {

    private val args: ReaderFragmentArgs by navArgs()

    private val viewModelFactory: ReaderViewModelFactory by lazy {
        ReaderViewModelFactory(requireContext().applicationContext, args.book, args.chapter)
    }

    private val viewModel: ReaderViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReaderViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvText.typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/sblgreek.ttf")
        tvText.setTextIsSelectable(false)
        tvText.movementMethod = AppConstants.createMovementMethod(requireContext())

        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.spannedText.observe(viewLifecycleOwner) {
            tvText.text = it
        }

        viewModel.selectedWordId.observe(viewLifecycleOwner) {
            tvText.refreshDrawableState()
            tvText.invalidate()
        }

        viewModel.selectedWord.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Clicked ${it.lex}", Toast.LENGTH_LONG).show()
        }
    }
}