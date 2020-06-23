package com.mattrobertson.greek.reader.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mattrobertson.greek.reader.R
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        bottom_nav_view.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            home_toolbar_title.text = when (destination.id) {
                R.id.navigation_home -> "SBLGNT Reader"
                R.id.navigation_plans -> "Plans"
                R.id.navigation_vocab -> "Vocab"
                else -> "SBLGNT Reader"
            }
        }
    }

}