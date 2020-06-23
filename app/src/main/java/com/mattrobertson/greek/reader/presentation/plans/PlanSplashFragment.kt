package com.mattrobertson.greek.reader.presentation.plans

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.mattrobertson.greek.reader.AppConstants
import com.mattrobertson.greek.reader.PlanReaderActivity
import com.mattrobertson.greek.reader.R
import kotlinx.android.synthetic.main.plan_splash.*
import java.util.*

class PlanSplashFragment: Fragment() {

    private val args: PlanSplashFragmentArgs by navArgs()

//    private val viewModelFactory: PlanSplashViewModelFactory by lazy {
//        PlanSplashViewModelFactory(requireContext().applicationContext)
//    }
//
//    private val viewModel: PlanSplashViewModel by lazy {
//        ViewModelProvider(this, viewModelFactory).get(PlanSplashViewModel::class.java)
//    }

    var plan = 0
    var day = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.plan_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val prefs = getDefaultSharedPreferences(requireContext())

        plan = args.plan

        tvTitle.text = AppConstants.READING_PLAN_TITLES[plan]
        tvDesc.text = AppConstants.READING_PLAN_DESCS[plan]

        var btn: Button
        val buttons = ArrayList<Button>()

        val params = LinearLayout.LayoutParams(200, 200)
        params.setMargins(0, 0, 5, 0)

        for (i in AppConstants.READING_PLANS[plan].indices) {
            btn = Button(requireContext())
            btn.text = (i + 1).toString()
            btn.tag = i.toString()
            btn.layoutParams = params
            btn.textSize = 16f
            btn.setBackgroundResource(R.drawable.day_square)
            btn.setTextColor(Color.parseColor("#000000"))
            btn.setOnClickListener { v ->
                buttons[day].setBackgroundResource(R.drawable.day_square)
                buttons[day].setTextColor(Color.parseColor("#000000"))
                day = v.tag.toString().toInt()
                v.setBackgroundResource(R.drawable.day_square_focus)
                updateChaptersText(
                        AppConstants.READING_PLANS[plan][day][0],
                        AppConstants.READING_PLANS[plan][day][1]
                )
            }
            buttons.add(btn)
            daysContainer.addView(btn)
        }

        buttons[0].performClick()

        btnBegin.setOnClickListener(View.OnClickListener {
            val i = Intent(requireContext(), PlanReaderActivity::class.java)
            i.putExtra("plan", plan)
            startActivity(i)
        })
    }

    private fun updateChaptersText(books: IntArray, chapters: IntArray) {
        var strText = ""
        for (i in books.indices) {
            strText += """${AppConstants.abbrvs[books[i]]} ${chapters[i]}"""
        }
        tvChapters.text = strText.trim { it <= ' ' }
    }
}