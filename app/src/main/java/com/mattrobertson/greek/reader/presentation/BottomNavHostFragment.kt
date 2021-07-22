package com.mattrobertson.greek.reader.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.databinding.BottomNavHostFragmentBinding

class BottomNavHostFragment : Fragment() {

    private var _binding: BottomNavHostFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomNavHostFragmentBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            // Set toolbar title
            when (destination.id) {
                R.id.navigation_plans -> binding.toolbarTitle.text = "Plans"
                R.id.navigation_vocab -> binding.toolbarTitle.text = "Vocabulary"
                R.id.navigation_more -> binding.toolbarTitle.text = "More"
            }

            // Swap toolbar title view (Reader uses a different view with a right drawable dropdown arrow)
            when (destination.id) {
                R.id.navigation_read -> {
                    binding.toolbarTitle.visibility = View.GONE
                    binding.toolbarReaderTitle.visibility = View.VISIBLE
                }
                else -> {
                    binding.toolbarReaderTitle.visibility = View.GONE
                    binding.toolbarTitle.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

}