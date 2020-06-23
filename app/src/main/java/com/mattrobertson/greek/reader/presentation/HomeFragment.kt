package com.mattrobertson.greek.reader.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mattrobertson.greek.reader.R
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                        HomeFragmentDirections.homeToSettings()
                )
            }
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(home_toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

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