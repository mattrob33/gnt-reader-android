package com.mattrobertson.greek.reader.presentation.reader

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
        val view = inflater.inflate(R.layout.concordance, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_concordance, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
            }

            R.id.menu_show_text -> {
                viewModel.showText = !viewModel.showText
            }
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(concordance_toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
        concordance_toolbar_title.text = args.lex

        concordance_text.movementMethod = LinkMovementMethod.getInstance()

        subscribeUI()

        viewModel.loadConcordance(args.lex)
    }

    private fun subscribeUI() {
        viewModel.screenState.observe(viewLifecycleOwner) {
            when (it) {
                ScreenState.LOADING -> {
                    concordance_progress_bar.visibility = View.VISIBLE
                }
                ScreenState.READY -> {
                    concordance_progress_bar.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.concordanceInfo.observe(viewLifecycleOwner) {
            it?.let { info ->
                concordance_text.text = info
            }
        }

        viewModel.verseRefSelected.observe(viewLifecycleOwner) {
            it?.let {  ref ->
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        CoreNavigationDirections.toReader(ref.book, ref.chapter)
                )
            }
        }

        viewModel.loadingProgress.observe(viewLifecycleOwner) {
            concordance_progress_bar.progress = it
        }
    }

}