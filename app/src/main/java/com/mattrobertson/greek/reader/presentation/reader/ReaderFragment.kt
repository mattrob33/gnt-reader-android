package com.mattrobertson.greek.reader.presentation.reader

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.data.VerseDatabase
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.presentation.util.ScreenState
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.ui.ReaderJsInterface
import kotlinx.android.synthetic.main.reader.*


class ReaderFragment : Fragment() {

    private val args: ReaderFragmentArgs by navArgs()

    private val verseRepo: VerseRepo by lazy {
        VerseRepo(VerseDatabase.getInstance().versesDao())
    }

    private val viewModelFactory: ReaderViewModelFactory by lazy {
        ReaderViewModelFactory(requireContext().applicationContext, verseRepo, Book(args.book), args.chapter)
    }

    private val viewModel: ReaderViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReaderViewModel::class.java)
    }

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.reader, container, false)
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!viewModel.navigateBack())
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
        }

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
            }
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        webview_reader.settings.apply {
            javaScriptEnabled = true
        }

        webview_reader.addJavascriptInterface(ReaderJsInterface(viewModel), "ReaderApp")

        webview_reader.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val anchorScroll = "javascript:scrollToAnchor(\"${args.book}_${args.chapter}_1\");"
                webview_reader.loadUrl(anchorScroll)
            }
        }

        tvConcordance.movementMethod = LinkMovementMethod.getInstance()

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.peekHeight = 400
        mBottomSheetBehavior.isHideable = true

        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ScreenState.LOADING -> {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
                    // TODO : show progress indicator
                }
                ScreenState.READY -> {
                    // TODO : hide progress indicator
                }
            }
        }

        viewModel.title.observe(viewLifecycleOwner) {
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            actionBar?.title = it
        }

        viewModel.html.observe(viewLifecycleOwner) { html ->

            val start = "<html><head>" +
                            "<style type=\"text/css\">" +
                                "@font-face { font-family: SblGreek; src: url(\"file:///android_asset/fonts/sblgreek.ttf\") }" +
                                "body {" +
                                    "font-family: SblGreek;" +
                                    "font-size: large;" +
                                    "line-height: 180%;" +
                                "}" +
                                "p {" +
                                    "margin: 0;" +
                                    "padding: 0;" +
                                "}" +
                            "</style>" +
                        "</head>" +
                        "<body>"+
                            "<script type=\"text/javascript\">" +
                                "function scrollToAnchor(id) { window.location.hash = id; } " +
                                "function onWordClick(text, lexicalForm, codedParsing) { ReaderApp.onWordClick(text, lexicalForm, codedParsing); }" +
                            "</script>"
            val end = "</body></html>"

            val styledHtml = start + html + end

            webview_reader.loadDataWithBaseURL(null, styledHtml, "text/html", "utf-8", null)
        }

        viewModel.glossInfo.observe(viewLifecycleOwner) { nullableGlossInfo ->
            nullableGlossInfo?.let { glossInfo ->
                var defEntry = glossInfo.gloss

                if (glossInfo.parsing.isNotBlank())
                    defEntry += "\n${glossInfo.parsing}"

                tvDef.text = defEntry
                tvLex.text = glossInfo.lex

                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        viewModel.concordanceInfo.observe(viewLifecycleOwner) { concordanceInfo ->
            tvConcordance.text = concordanceInfo ?: ""
        }

        viewModel.showConcordanceScreenForLex.observe(viewLifecycleOwner) { lex ->
            launchConcordanceScreen(lex)
        }
    }

    private fun launchConcordanceScreen(lex: String) {
        requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
            ReaderFragmentDirections.toConcordance(lex)
        )
    }
}