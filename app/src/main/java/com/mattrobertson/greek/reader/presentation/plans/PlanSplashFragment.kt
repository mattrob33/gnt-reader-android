package com.mattrobertson.greek.reader.presentation.plans

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mattrobertson.greek.reader.PlanReaderActivity
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.databinding.PlanSplashBinding
import com.mattrobertson.greek.reader.util.dpToPx
import java.util.*

class PlanSplashFragment: Fragment() {

    private var _binding: PlanSplashBinding? = null
    private val binding get() = _binding!!

    private val args: PlanSplashFragmentArgs by navArgs()

    private val viewModelFactory: PlanSplashViewModelFactory by lazy {
        PlanSplashViewModelFactory(args.plan)
    }

    private val viewModel: PlanSplashViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PlanSplashViewModel::class.java)
    }

    var plan = 0
    var day = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PlanSplashBinding.inflate(inflater, container, false)

        plan = args.plan

        binding.tvTitle.text = viewModel.planTitle
        binding.tvDesc.text = viewModel.planDesc

        var btn: Button
        val buttons = ArrayList<Button>()

        val btnWidth = dpToPx(100)
        val params = LinearLayout.LayoutParams(btnWidth, btnWidth)
        params.setMargins(0, 0, dpToPx(2), 0)

        for (i in 0 until viewModel.numDays) {
            btn = Button(requireContext())
            btn.text = (i + 1).toString()
            btn.tag = i.toString()
            btn.layoutParams = params
            btn.textSize = 16f
            btn.setBackgroundResource(R.drawable.day_square)
            btn.setOnClickListener { v ->
                buttons[day].setBackgroundResource(R.drawable.day_square)
                day = v.tag.toString().toInt()
                v.setBackgroundResource(R.drawable.day_square_focus)
                binding.tvChapters.text = viewModel.getPreviewForDay(day)
            }
            buttons.add(btn)
            binding.daysContainer.addView(btn)
        }

        buttons[0].performClick()

        binding.btnBegin.setOnClickListener {
            val i = Intent(requireContext(), PlanReaderActivity::class.java)
            i.putExtra("plan", plan)
            startActivity(i)
        }

        return binding.root
    }
}