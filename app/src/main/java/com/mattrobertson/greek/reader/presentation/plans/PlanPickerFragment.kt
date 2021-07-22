package com.mattrobertson.greek.reader.presentation.plans

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mattrobertson.greek.reader.PlanReaderActivity
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.databinding.PlanPickerFragmentBinding
import com.mattrobertson.greek.reader.presentation.BottomNavHostFragmentDirections

class PlanPickerFragment: Fragment() {

    private var _binding: PlanPickerFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModelFactory: PlanPickerViewModelFactory by lazy {
        PlanPickerViewModelFactory(requireContext().applicationContext)
    }

    private val viewModel: PlanPickerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PlanPickerViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PlanPickerFragmentBinding.inflate(inflater, container, false)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, viewModel.plans)
        binding.lvPlan.adapter = adapter
        binding.lvPlan.setOnItemClickListener { _, _, plan, _ ->
            launchPlan(plan)
        }

        return binding.root
    }

    private fun launchPlan(plan: Int) {
        if (viewModel.hasStartedPlan(plan)) {
            Intent(requireContext(), PlanReaderActivity::class.java).apply {
                putExtra("plan", plan)
                startActivity(this)
            }
        }
        else {
            requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                BottomNavHostFragmentDirections.toPlanSplash(plan)
            )
        }
    }

}