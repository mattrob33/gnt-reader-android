package com.mattrobertson.greek.reader.presentation.reader

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mattrobertson.greek.reader.CoreNavigationDirections
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.presentation.ScreenState
import kotlinx.android.synthetic.main.concordance.*

class ConcordanceFragment : Fragment() {


    private val args: ConcordanceFragmentArgs by navArgs()

    private val viewModelFactory: ConcordanceViewModelFactory by lazy {
        ConcordanceViewModelFactory(requireContext().applicationContext)
    }

    private val viewModel: ConcordanceViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ConcordanceViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.concordance, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvConcordanceTitle.text = args.lex
        tvConcordanceText.movementMethod = LinkMovementMethod.getInstance()

        subscribeUI()

        viewModel.loadConcordance(args.lex)
    }

    private fun subscribeUI() {
        viewModel.screenState.observe(viewLifecycleOwner) {
            when (it) {
                ScreenState.LOADING -> {
                    concordanceProgressBar.visibility = View.VISIBLE
                }
                ScreenState.READY -> {
                    concordanceProgressBar.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.concordanceInfo.observe(viewLifecycleOwner) {
            it?.let { info ->
                tvConcordanceText.text = info
            }
        }

        viewModel.verseRefSelected.observe(viewLifecycleOwner) {
            it?.let {  ref ->
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        CoreNavigationDirections.toReader(ref.book, ref.chapter)
                )
            }
        }
    }

}