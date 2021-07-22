package com.mattrobertson.greek.reader.presentation.concordance

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
import com.mattrobertson.greek.reader.databinding.ConcordanceBinding
import com.mattrobertson.greek.reader.presentation.util.ScreenState

class ConcordanceFragment : Fragment() {

    private var _binding: ConcordanceBinding? = null
    private val binding get() = _binding!!

    private val args: ConcordanceFragmentArgs by navArgs()

    private val viewModelFactory: ConcordanceViewModelFactory by lazy {
        ConcordanceViewModelFactory(requireContext().applicationContext)
    }

    private val viewModel: ConcordanceViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ConcordanceViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ConcordanceBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).setSupportActionBar(binding.concordanceToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.concordanceToolbarTitle.text = args.lex

        binding.concordanceText.movementMethod = LinkMovementMethod.getInstance()

        subscribeUI()

        viewModel.loadConcordance(args.lex)

        return binding.root
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

    private fun subscribeUI() {
        viewModel.screenState.observe(viewLifecycleOwner) {
            when (it) {
                ScreenState.LOADING -> {
                    binding.concordanceProgressBar.visibility = View.VISIBLE
                }
                ScreenState.READY -> {
                    binding.concordanceProgressBar.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.concordanceInfo.observe(viewLifecycleOwner) {
            it?.let { info ->
                binding.concordanceText.text = info
            }
        }

        viewModel.verseRefSelected.observe(viewLifecycleOwner) {
            it?.let {  ref ->
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        CoreNavigationDirections.toReader(ref.book.num, ref.chapter)
                )
            }
        }

        viewModel.loadingProgress.observe(viewLifecycleOwner) {
            binding.concordanceProgressBar.progress = it
        }
    }

}