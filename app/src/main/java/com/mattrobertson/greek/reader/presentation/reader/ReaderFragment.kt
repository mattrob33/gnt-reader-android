package com.mattrobertson.greek.reader.presentation.reader

import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
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

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.reader, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!viewModel.navigateBack())
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvText.typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/sblgreek.ttf")
        tvText.setTextIsSelectable(false)
        tvText.movementMethod = AppConstants.createMovementMethod(requireContext())

        tvConcordance.movementMethod = LinkMovementMethod.getInstance()

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.peekHeight = 400
        mBottomSheetBehavior.isHideable = true

        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ReaderState.LOADING -> {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
                    // TODO : show progress indicator
                }
                ReaderState.READY -> {
                    // TODO : hide progress indicator
                }
            }
        }

        viewModel.spannedText.observe(viewLifecycleOwner) {
            tvText.text = it
            tvText.invalidate()
        }

        viewModel.selectedWordId.observe(viewLifecycleOwner) {
            tvText.refreshDrawableState()
            tvText.invalidate()
        }

        viewModel.glossInfo.observe(viewLifecycleOwner) { nullableGlossInfo ->
            nullableGlossInfo?.let { glossInfo ->
                var defEntry = glossInfo.gloss

                if (glossInfo.parsing.isNotBlank())
                    defEntry += "\n${glossInfo.parsing}"

                tvDef.text = defEntry
                tvLex.text = glossInfo.lex

                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        viewModel.concordanceInfo.observe(viewLifecycleOwner) { concordanceInfo ->
            tvConcordance.text = concordanceInfo ?: ""
        }

        viewModel.concordanceItemSelected.observe(viewLifecycleOwner) { item ->
//            item?.let {
//                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
//                        CoreNavigationDirections.toReader(it.book, it.chapter)
//                )
//            }
        }
    }
}