package com.mattrobertson.greek.reader.presentation

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mattrobertson.greek.reader.R
import kotlinx.android.synthetic.main.about.*

class AboutFragment: Fragment() {

    private val sbl = "<p>Scripture is from the <a href='http://sblgnt.com'>SBL Greek New Testament</a>. Copyright © 2010 <a href='http://www.sbl-site.org/'>Society of Biblical Literature</a> and <a href='http://www.logos.com/'>Logos Bible Software</a>.</p>"
    private val morph = "<p>The <a href='https://github.com/morphgnt/sblgnt'>MorphGNT SBLGNT</a> project is used as the text base to identify lexical forms.</p>"
    private val mounce = "<p>Dictionary entries from:<br /><a href='https://github.com/billmounce/dictionary'>Mounce Concise Greek-English Dictionary</a><br />Copyright 1993 All Rights Reserved<br /><a href='http://www.teknia.com/greek-dictionary'>www.teknia.com/greek-dictionary</a></p>"
    private val audio = "<p>Audio is from <a href='http://www.greeknewtestamentaudio.com'>GreekNewTestamentAudio.com</a> (formerly GreekLatinAudio.com). Used with permission.</p>"

    private val aboutText = sbl+"\n"+morph+"\n"+mounce+"\n"+audio

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.about, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(about_toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        tvAbout.text = Html.fromHtml(aboutText)
        tvAbout.movementMethod = LinkMovementMethod.getInstance()
        tvAbout.highlightColor = Color.TRANSPARENT
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return true
    }

}