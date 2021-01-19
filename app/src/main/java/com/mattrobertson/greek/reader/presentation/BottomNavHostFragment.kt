package com.mattrobertson.greek.reader.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mattrobertson.greek.reader.R
import kotlinx.android.synthetic.main.home_fragment.*


class BottomNavHostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        bottom_nav_view.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_plans -> toolbar_title.text = "Plans"
                R.id.navigation_vocab -> toolbar_title.text = "Vocabulary"
                R.id.navigation_more -> toolbar_title.text = "More"
            }
        }
    }

}