package com.mattrobertson.greek.reader.presentation.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mattrobertson.greek.reader.R

class PlanPickerFragment : Fragment() {

    companion object {
        fun newInstance() = PlanPickerFragment()
    }

    private lateinit var viewModel: PlanPickerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.plan_picker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlanPickerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}