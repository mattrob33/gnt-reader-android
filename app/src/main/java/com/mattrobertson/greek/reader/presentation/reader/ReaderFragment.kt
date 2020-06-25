package com.mattrobertson.greek.reader.presentation.reader

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvText.typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/sblgreek.ttf")
        tvText.setTextIsSelectable(false)
        tvText.movementMethod = AppConstants.createMovementMethod(requireContext())

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.peekHeight = 400
        mBottomSheetBehavior.isHideable = true

        requireActivity().window.decorView.post {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ReaderState.LOADING -> {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
                }
                ReaderState.READY -> {

                }
            }
        }

        viewModel.spannedText.observe(viewLifecycleOwner) {
            tvText.text = it
        }

        viewModel.selectedWordId.observe(viewLifecycleOwner) {
            tvText.refreshDrawableState()
            tvText.invalidate()
        }

        viewModel.glossInfo.observe(viewLifecycleOwner) { nullableGlossInfo ->
            nullableGlossInfo?.let { glossInfo ->
                var strDefDisplay: String = glossInfo.gloss

                if (glossInfo.parsing.isNotBlank()) {
                    strDefDisplay += "\n${glossInfo.parsing}"
                }

                tvLex.text = glossInfo.lex
                tvDef.text = strDefDisplay

                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}