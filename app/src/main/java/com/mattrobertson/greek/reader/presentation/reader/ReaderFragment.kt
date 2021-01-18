package com.mattrobertson.greek.reader.presentation.reader

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.presentation.HomeFragmentDirections
import com.mattrobertson.greek.reader.presentation.util.ScreenState
import com.mattrobertson.greek.reader.ui.ReaderJsInterface
import com.mattrobertson.greek.reader.ui.SwipeDetector
import com.mattrobertson.greek.reader.ui.Swipeable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.reader.*


@AndroidEntryPoint
class ReaderFragment : Fragment() {

    private val viewModel by viewModels<ReaderViewModel>()

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var toolbarTitle: TextView

    private lateinit var bottomSheet: NestedScrollView
    private lateinit var tvConcordance: TextView
    private lateinit var tvDef: TextView
    private lateinit var tvLex: TextView

    lateinit var rootView: View

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.reader, container, false)
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!viewModel.navigateBack())
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
        }

        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().findNavController(R.id.core_nav_host_fragment).navigateUp()
            }
        }
        return true
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title)

        bottomSheet = requireActivity().findViewById(R.id.bottomSheet)
        tvConcordance = requireActivity().findViewById(R.id.tvConcordance)
        tvDef = requireActivity().findViewById(R.id.tvDef)
        tvLex = requireActivity().findViewById(R.id.tvLex)

        bottomSheet.visibility = View.VISIBLE

        webview_reader.settings.apply {
            javaScriptEnabled = true
        }

        val nightModeFlags = requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            if (isNightMode) {
                WebSettingsCompat.setForceDark(webview_reader.settings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                WebSettingsCompat.setForceDark(webview_reader.settings, WebSettingsCompat.FORCE_DARK_OFF)
            }
        }

        webview_reader.addJavascriptInterface(ReaderJsInterface(viewModel), "ReaderApp")

        tvConcordance.movementMethod = LinkMovementMethod.getInstance()

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.peekHeight = 400
        mBottomSheetBehavior.isHideable = true

        toolbarTitle.setOnClickListener {
            requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                HomeFragmentDirections.toBookPicker()
            )
        }

        subscribeUI()

        val swipeHandler = object: Swipeable {
            override fun onSwipeLeft() {
                viewModel.nextChapter()
            }

            override fun onSwipeRight() {
                viewModel.prevChapter()
            }
        }

        val swipeDetector = SwipeDetector(swipeHandler)

        val gestureDetector = GestureDetector(requireContext(), swipeDetector)

        webview_reader.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
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
            toolbarTitle.text = it
        }

        viewModel.html.observe(viewLifecycleOwner) { html ->
            webview_reader.loadDataWithBaseURL("app:html", html, "text/html", "utf-8", null)
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