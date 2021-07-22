package com.mattrobertson.greek.reader.presentation.refpicker

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.databinding.RefPickerFragmentBinding
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.presentation.reader.ReaderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RefPickerFragment : Fragment() {

    private var _binding: RefPickerFragmentBinding? = null
    private val binding get() = _binding!!

    private val readerViewModel by activityViewModels<ReaderViewModel>()
    private val refPickerViewModel by activityViewModels<RefPickerViewModel>()

    private lateinit var refPickerAdapter: RefPickerAdapter

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RefPickerFragmentBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        refPickerAdapter = RefPickerAdapter(this)
        binding.refPickerViewPager.adapter = refPickerAdapter

        TabLayoutMediator(binding.refPickerTabLayout, binding.refPickerViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Book"
                1 -> "Chapter"
                else -> ""
            }
        }.attach()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = requireActivity().findNavController(R.id.core_nav_host_fragment)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.refPickerToolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }

        refPickerViewModel.chapter.value = 0
        refPickerViewModel.book.value = null

        refPickerViewModel.book.observe(viewLifecycleOwner) { book ->
            book?.let {
                binding.refPickerViewPager.post {
                    refPickerAdapter.notifyDataSetChanged()
                    binding.refPickerViewPager.currentItem = 1
                }
            }
        }

        refPickerViewModel.chapter.observe(viewLifecycleOwner) { chapter ->
            if (chapter > 0) {
                val book = refPickerViewModel.book.value!!
                readerViewModel.goTo(VerseRef(book, chapter))
                navController.navigateUp()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_ref_picker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }

            R.id.menu_recent_refs -> {
                navController.navigate(
                    RefPickerFragmentDirections.toRecentRefs()
                )
            }
        }
        return true
    }

    class RefPickerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BookPickerFragment()
                1 -> ChapterPickerFragment()
                else -> throw IllegalStateException("RefPickerAdapter only has 2 items but position was $position")
            }
        }
    }
}